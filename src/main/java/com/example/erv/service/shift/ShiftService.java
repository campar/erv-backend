package com.example.erv.service.shift;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Shift;
import com.example.erv.model.User;
import com.example.erv.repository.shift.ShiftRepository;
import com.example.erv.repository.timetable.TimetableRepository;
import com.example.erv.service.user.UserAttribute;
import com.example.erv.service.user.UserService;
import com.example.erv.specification.SpecificationBasic;

@Service
public class ShiftService extends ShiftServiceBasic {
    @Autowired
    private UserService userService;

    @Autowired
    private TimetableRepository timetableRepository;

    public ShiftService(ShiftRepository repository) {
        super(repository);
    }

    private static class ShiftTarget {
        Shift shift;
        Long durationIndex;
        Long endFromIndex;
        Long endToIndex;
        Long startFromIndex;
        Long startToIndex;

        public ShiftTarget(Shift shift, Long durationIndex, Long endFromIndex, Long endToIndex, Long startFromIndex, Long startToIndex) {
            this.shift = shift;
            this.durationIndex = durationIndex;
            this.endFromIndex = endFromIndex;
            this.endToIndex = endToIndex;
            this.startFromIndex = startFromIndex;
            this.startToIndex = startToIndex;
        }
    }

    public Shift findShiftForCheckOutTime(ZonedDateTime checkOut, ZonedDateTime checkIn, Set<Shift> shifts) {
        LocalTime checkOutTime = checkOut.toLocalTime();
        LocalTime checkInTime = checkIn.toLocalTime();

        Duration actualDuration = Duration.between(checkInTime, checkOutTime);

        ShiftTarget target = null;

        for (Shift shift : shifts) {
            long durationIndex = Math.abs(shift.getDuration().minus(actualDuration).toMinutes());
            long endFromIndex = Math.abs(Duration.between(checkOutTime, shift.getEndFromTime()).toMinutes());
            long endToIndex = Math.abs(Duration.between(checkOutTime, shift.getEndToTime()).toMinutes());
            long startFromIndex = Math.abs(Duration.between(checkInTime, shift.getStartFromTime()).toMinutes());
            long startToIndex = Math.abs(Duration.between(checkInTime, shift.getStartToTime()).toMinutes());
            ShiftTarget potential = new ShiftTarget(shift, durationIndex, endFromIndex, endToIndex, startFromIndex, startToIndex);

            // Initializing the comparison value
            if (target == null) {
                target = potential;
                continue;
            }
            // If the duration of the comparing shift is more accurate than current target (closer to zero)
            if (durationIndex <= target.durationIndex && isMoreAccurate(potential, target)) {
                target = potential;
            }
        }
        return target == null ? null : target.shift;
    }

    private static boolean isMoreAccurate(ShiftTarget potential, ShiftTarget target) {
        return (potential.endFromIndex < target.endFromIndex && potential.startFromIndex < target.startFromIndex) ||
                (potential.endToIndex < target.endToIndex && potential.startFromIndex < target.endFromIndex);
    }

    public Shift findShiftForCheckInTime(ZonedDateTime checkInTime, Set<Shift> shifts) {
        LocalTime time = checkInTime.toLocalTime();

        // Find the shift with the closest start time
        return shifts.stream()
                .min((shift1, shift2) -> {
                    LocalTime shift1Time = shift1.getStartFromTime();
                    LocalTime shift2Time = shift2.getStartFromTime();

                    long diff1 = Math.abs(shift1Time.until(time, java.time.temporal.ChronoUnit.MINUTES));
                    long diff2 = Math.abs(shift2Time.until(time, java.time.temporal.ChronoUnit.MINUTES));
                    if (diff1 == diff2) {
                        // If two shifts have the same start time difference, return the one with the later startsFrom
                        return shift2Time.compareTo(shift1Time);
                    }
                    return Long.compare(diff1, diff2);
                }).orElseThrow();
    }

    @Override
    public List<Shift> findAll(Specification<Shift> specification) {
        SpecificationBasic<Shift> spec = SpecificationBasic.where(ShiftAttribute.SOFT_DELETED, false);
        return super.findAll(spec.and(specification).getSpecification());
    }

    @Override
    public List<Shift> findAll() {
        SpecificationBasic<Shift> spec = SpecificationBasic.where(ShiftAttribute.SOFT_DELETED, false);
        return super.findAll(spec.getSpecification());
    }

    @Override
    public Page<Shift> findAll(Specification<Shift> specification, Pageable pageable) {
        SpecificationBasic<Shift> spec = SpecificationBasic.where(ShiftAttribute.SOFT_DELETED, false);
        return super.findAll(spec.and(specification).getSpecification(), pageable);
    }

    @Override
    public Page<Shift> findAll(Pageable pageable) {
        SpecificationBasic<Shift> spec = SpecificationBasic.where(ShiftAttribute.SOFT_DELETED, false);
        return super.findAll(spec.getSpecification(), pageable);
    }

    /**
     * If the Shift object has usages either in a relationship with the User or a Timetable object we can't just delete it, so we soft-delete it.
     * Else, the Shift will be fully deleted.
     *
     * @param id of the shift.
     */
    @Override
    public void deleteById(Long id) throws ErvException {
        Shift shift = repository.findById(id).orElseThrow();


        SpecificationBasic<User> userSpec = SpecificationBasic.<User>get().joinAnd(UserAttribute.SHIFTS, ShiftAttribute.ID, id);
        long timetableUsage = timetableRepository.countByShift(shift);
        long userUsage = userService.count(userSpec.getSpecification());

        if (timetableUsage == 0 && userUsage == 0) {
            super.deleteById(id);
        } else {
            softDelete(userSpec, shift);
        }
    }

    private void softDelete(SpecificationBasic<User> userFilter, Shift shift, Shift replacement) throws ErvException {
        List<User> users = userService.findAll(userFilter.getSpecification());
        for (User user : users) {
            user.getShifts().remove(shift);
            if (replacement != null) {
                user.getShifts().add(replacement);
            }
            userService.update(user);
        }
        shift.setSoftDeleted(true);
        super.update(shift);
    }

    private void softDelete(SpecificationBasic<User> userFilter, Shift shift) throws ErvException {
        softDelete(userFilter, shift, null);
    }

    /**
     * If the Shift object has usages either in a relationship with the User or a Timetable object we can't just delete it, so we soft-delete it.
     * Else, the Shift will be fully deleted.
     */
    @Override
    @Transactional
    public Shift update(Shift object) throws ErvException {
        Shift beforeUpdate = repository.findByIdWithLock(object.getId()).orElseThrow(() -> new ErvException("Can't update non existing object!"));
        if(beforeUpdate.getSoftDeleted()) return object;
        if (shiftEquals(object, beforeUpdate)) {
            return object;
        }

        SpecificationBasic<User> userSpec = SpecificationBasic.<User>get().joinAnd(UserAttribute.SHIFTS, ShiftAttribute.ID, object.getId());

        long timetableUsage = timetableRepository.countByShift(object);
        long userUsage = userService.count(userSpec.getSpecification());

        if (timetableUsage == 0 && userUsage == 0) {
            return super.update(object);
        } else {
        	beforeUpdate.setSoftDeleted(true);
        	super.save(beforeUpdate);
            object.setId(null);
            object = super.insert(object);
            softDelete(userSpec, beforeUpdate, object);
            return object;
        }
    }

    private boolean shiftEquals(Shift object, Shift beforeUpdate) {
        return object.getSoftDeleted().equals(beforeUpdate.getSoftDeleted()) &&
                object.getDurationHours().equals(beforeUpdate.getDurationHours()) &&
                object.getDurationMinutes().equals(beforeUpdate.getDurationMinutes()) &&
                object.getEndTo().equals(beforeUpdate.getEndTo()) &&
                object.getEndFrom().equals(beforeUpdate.getEndFrom()) &&
                object.getStartFrom().equals(beforeUpdate.getStartFrom()) &&
                object.getStartTo().equals(beforeUpdate.getStartTo());

    }
}
