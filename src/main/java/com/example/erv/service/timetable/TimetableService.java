package com.example.erv.service.timetable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.ActionCount;
import com.example.erv.dto.AttendanceRecordDTO;
import com.example.erv.exceptions.InvalidActionException;
import com.example.erv.exceptions.LocalizationKey;
import com.example.erv.exceptions.ErvException;
import com.example.erv.exceptions.NoHardwareIdentifierException;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Shift;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.timetable.CheckInStatsDTO;
import com.example.erv.repository.timetable.TimetableRepository;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.example.erv.service.device.DeviceService;
import com.example.erv.service.shift.ShiftService;
import com.example.erv.service.user.UserAttribute;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.SecurityUtils;

import lombok.Getter;

@Service
public class TimetableService extends TimetableServiceBasic {

	@Autowired
	private DeviceService deviceService;
	@Autowired
	private ShiftService shiftService;
	@Autowired
	@Getter
	private SecurityUtils securityUtils;
	@Autowired
	@Getter
	private DepartmentUnitService departmentUnitService;

	public TimetableService(TimetableRepository repository) {
		super(repository);
	}

	public List<Timetable> findByUserAndDateRange(User user, ZonedDateTime startDate, ZonedDateTime endDate) {
		SpecificationBasic<Timetable> spec = SpecificationBasic.<Timetable>where(TimetableAttribute.USER, user).and(TimetableAttribute.DATE_TIME, SearchOperator.BETWEEN, List.of(startDate, endDate))
				.and(TimetableAttribute.ACTION_TYPE, ActionType.CHECK_IN);
		List<Timetable> result = findAll(spec.getSpecification());
		List<Timetable> resultWithGroupedTimetables = new ArrayList<>();
		for (Timetable timetable : result) {
			resultWithGroupedTimetables.addAll(findByGroupingKey(timetable.getId()));
		}

		return resultWithGroupedTimetables;
	}

	public List<Timetable> findByUserAndShiftAndDateRange(User user, List<Shift> shifts, ZonedDateTime startDate, ZonedDateTime endDate) {
		SpecificationBasic<Timetable> spec = SpecificationBasic.<Timetable>where(TimetableAttribute.USER, user).and(TimetableAttribute.DATE_TIME, SearchOperator.BETWEEN, List.of(startDate, endDate))
				.and(TimetableAttribute.ACTION_TYPE, ActionType.CHECK_IN);
		if (!shifts.isEmpty()) {
			spec.and(TimetableAttribute.SHIFT, SearchOperator.IN, shifts);
		}
		List<Timetable> result = findAll(spec.getSpecification());
		List<Timetable> resultWithGroupedTimetables = new ArrayList<>();
		for (Timetable timetable : result) {
			resultWithGroupedTimetables.addAll(findByGroupingKey(timetable.getId()));
		}

		return resultWithGroupedTimetables;
	}

	public List<CheckInStatsDTO> findCheckInStats(List<Long> devices, ZonedDateTime startDate, ZonedDateTime endDate) {
		return repository.findCheckInStats(devices, startDate.toInstant(), endDate.toInstant());
	}

	public void registerAction(AttendanceRecordDTO attendanceRecordDTO, User user) throws ErvException {
		Timetable timetable = new Timetable();
		timetable.setActionType(attendanceRecordDTO.getActionType());
		timetable.setUser(user);
		if (attendanceRecordDTO.getDeviceId() != null) {
			timetable.setDevice(deviceService.findByHardwareIdentifier(attendanceRecordDTO.getDeviceId()));
		} else {
			throw new NoHardwareIdentifierException("Device identifier is not configured on device.");
		}
		if (attendanceRecordDTO.getDateTime() == null) {
			timetable.setDateTime(Instant.now());
		} else {
			timetable.setDateTime(attendanceRecordDTO.getDateTime().toInstant());
		}
		insert(timetable);
	}

	/**
	 * If the user checks in our checks out we have to identify the shift to set to
	 * the timetable object. <br>
	 * If it's any other action we have to take the shift from check in action and
	 * set it to the timetable object.
	 *
	 * @param user      who performs an action.
	 * @param timetable object that contains action information.
	 */
	private void setShiftByAction(User user, Timetable timetable, List<Timetable> timetableHistory) throws ErvException {
		switch (timetable.getActionType()) {
		case CHECK_IN -> setCheckInShift(user, timetable);
		case CHECK_OUT -> setCheckOutShift(user, timetable, timetableHistory);
		case PAUSE_END, PAUSE_START, BUSINESS_START, BUSINESS_END -> setShiftFromCheckInAction(timetable, timetableHistory);
		}
	}

	private void setShiftFromCheckInAction(Timetable timetable, List<Timetable> timetableHistory) throws InvalidActionException {
		Optional<Timetable> checkIn = findParentAction(timetableHistory);
		if (checkIn.isEmpty()) {
			throw new InvalidActionException("You haven't checked in today.");
		}
		timetable.setShift(checkIn.get().getShift());
	}

	private void setCheckOutShift(User user, Timetable timetable, List<Timetable> timetableHistory) throws UserEmptyShiftsException, InvalidActionException {
		if (user.getShifts().isEmpty()) {
			throw new UserEmptyShiftsException();
		}

		Optional<Timetable> checkIn = findParentAction(timetableHistory);
		if (checkIn.isEmpty()) {
			throw new InvalidActionException("You haven't checked in today.");
		}

		Shift shift = shiftService.findShiftForCheckOutTime(timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())),
				checkIn.get().getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())), user.getShifts());

		// Validate and update shifts, so it's consistent for the user for the grouping
		// key.
		for (Timetable action : findByGroupingKey(checkIn.get().getGroupingKey())) {
			if (action.getShift().equals(shift)) continue;
			action.setShift(shift);
			save(action);
		}

		timetable.setShift(shift);
	}

	private void setCheckInShift(User user, Timetable timetable) throws UserEmptyShiftsException {
		if (user.getShifts().isEmpty()) {
			throw new UserEmptyShiftsException();
		}
		Shift shift = shiftService.findShiftForCheckInTime(timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())), user.getShifts());
		timetable.setShift(shift);
	}

	@Override
	public Page<Timetable> findAll(Pageable pageable) {
		User user = securityUtils.getCurrentUser();
		if (SecurityUtils.isSuperUser(user)) {
			return super.findAll(pageable);
		}
		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
		SpecificationBasic<Timetable> spec = SpecificationBasic.<Timetable>get().joinAnd(TimetableAttribute.USER, UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible);
		return super.findAll(spec.getSpecification(), pageable);
	}

	@Override
	public Page<Timetable> findAll(Specification<Timetable> specification, Pageable pageable) {
		User user = securityUtils.getCurrentUser();
		if (SecurityUtils.isSuperUser(user)) {
			return super.findAll(specification, pageable);
		}
		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
		SpecificationBasic<Timetable> spec = SpecificationBasic.<Timetable>get().joinAnd(TimetableAttribute.USER, UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(specification);
		return super.findAll(spec.getSpecification(), pageable);
	}

	public Page<LocalDate> findReportDatesByRangeAndUser(ZonedDateTime dateFrom, ZonedDateTime dateTo, Long userId, List<ActionType> actionTypes, Pageable pageable) {
		return repository.findDatesByRangeAndUser(dateFrom.toInstant(), dateTo.toInstant(), userId, actionTypes.stream().map(ActionType::name).toList(), pageable);
	}

	public List<Timetable> findByDates(List<LocalDate> dates, User user, List<ActionType> actionTypes) {
		return repository.findByDates(dates, user.getId(), actionTypes.stream().map(ActionType::name).toList());
	}

	@Override
	public Timetable insert(Timetable timetable) throws ErvException {
		if (timetable.getId() != null) throw new ErvException("Entity already exists: " + timetable.getId());
		return performSave(timetable);
	}

	@Override
	public Timetable update(Timetable timetable) throws ErvException {
		return performSave(timetable);
	}

	private Timetable performSave(Timetable timetable) throws ErvException {
		validateDevice(timetable);
		List<Timetable> timetableHistory = getTimetableHistory(timetable);

		checkForDuplicateActions(timetable, timetableHistory);
		validateActions(timetable, timetableHistory);
		setShiftByAction(timetable.getUser(), timetable, timetableHistory);
		if (timetable.getActionType() == ActionType.CHECK_OUT) {
			closeActions(timetableHistory, timetable);
		}

		if (timetable.getActionType() != ActionType.CHECK_IN) {
			Optional<Timetable> parentAction = findParentAction(timetableHistory);
			if (parentAction.isEmpty()) throw new ErvException("No parent action.");
			timetable.setGroupingKey(parentAction.get().getGroupingKey());
		}
		return repository.save(timetable);
	}

	private List<Timetable> getTimetableHistory(Timetable timetable) {
		SpecificationBasic<Timetable> spec = SpecificationBasic.<Timetable>where(TimetableAttribute.USER, timetable.getUser()).and(TimetableAttribute.DATE_TIME, SearchOperator.BETWEEN,
				List.of(timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).minusHours(18), timetable.getDateTime()));
		return repository.findAll(spec.getSpecification());
	}

	private void validateDevice(Timetable timetable) throws InvalidActionException {
		if (Boolean.FALSE.equals(timetable.getDevice().getActive())) throw new InvalidActionException("Device is marked as inactive.", LocalizationKey.INACTIVE_DEVICE.getKey());
	}

	private Optional<Timetable> findParentAction(List<Timetable> timetableHistory) {
		return timetableHistory.stream().filter(e -> e.getActionType() == ActionType.CHECK_IN).findFirst();
	}

	private void checkForDuplicateActions(Timetable object, List<Timetable> timetableHistory) throws InvalidActionException {
		Map<ActionType, List<Timetable>> actionMap = timetableHistory.stream().collect(Collectors.groupingBy(Timetable::getActionType));
		if (object.getId() == null) {
			if (actionMap.containsKey(object.getActionType())) {
				switch (object.getActionType()) {
				case CHECK_IN: {
					throw new InvalidActionException("You have already checked in today.", LocalizationKey.ALREADY_CHECKED_IN.getKey());
				}
				case CHECK_OUT: {
					throw new InvalidActionException("You have already checked out today.", LocalizationKey.ALREADY_CHECKED_OUT.getKey());
				}
				case PAUSE_START: {
					throw new InvalidActionException("You have already started a pause.", LocalizationKey.ALREADY_STARTED_PAUSE.getKey());
				}
				case PAUSE_END: {
					throw new InvalidActionException("You have already ended a pause or haven't started one yet.", LocalizationKey.ALREADY_ENDED_PAUSE.getKey());
				}
				case BUSINESS_START: {
					throw new InvalidActionException("You have already registered a business entry.", LocalizationKey.BUSINESS_STARTED_ALREADY.getKey());
				}
				case BUSINESS_END: {
					throw new InvalidActionException("You have already registered a business exit.", LocalizationKey.BUSINESS_ENDED_ALREADY.getKey());
				}
				default:
					throw new InvalidActionException("Invalid action type: " + object.getActionType(), LocalizationKey.INVALID_ACTION.getKey());
				}
			}
		} else {
			Timetable timetable = findById(object.getId()).get();
			if (!object.getActionType().equals(timetable.getActionType()) && actionMap.containsKey(object.getActionType())) {
				switch (object.getActionType()) {
				case CHECK_IN: {
					throw new InvalidActionException("You have already checked in today.", LocalizationKey.ALREADY_CHECKED_IN.getKey());
				}
				case CHECK_OUT: {
					throw new InvalidActionException("You have already checked out today.", LocalizationKey.ALREADY_CHECKED_OUT.getKey());
				}
				case PAUSE_START: {
					throw new InvalidActionException("You have already started a pause.", LocalizationKey.ALREADY_STARTED_PAUSE.getKey());
				}
				case PAUSE_END: {
					throw new InvalidActionException("You have already ended a pause or haven't started one yet.", LocalizationKey.ALREADY_ENDED_PAUSE.getKey());
				}
				case BUSINESS_START: {
					throw new InvalidActionException("You have already registered a business entry.", LocalizationKey.BUSINESS_STARTED_ALREADY.getKey());
				}
				case BUSINESS_END: {
					throw new InvalidActionException("You have already registered a business exit.", LocalizationKey.BUSINESS_ENDED_ALREADY.getKey());
				}
				default:
					throw new InvalidActionException("Invalid action type: " + object.getActionType(), LocalizationKey.INVALID_ACTION.getKey());
				}
			}
		}
	}

	private void validateActions(Timetable object, List<Timetable> timetableHistory) throws InvalidActionException {
		Set<ActionType> existingActions = timetableHistory.stream().map(Timetable::getActionType).collect(Collectors.toSet());

		ActionType actionType = object.getActionType();

		switch (actionType) {
		case CHECK_OUT:
			validateCheckInRequired(existingActions);
			break;
		case PAUSE_END:
			validatePauseEnd(existingActions);
			break;
		case BUSINESS_END:
			validateBusinessEnd(existingActions);
			break;
		case PAUSE_START, BUSINESS_START:
			validateStartActions(existingActions);
			break;
		default:
		}
	}

	private void validateCheckInRequired(Set<ActionType> existingActions) throws InvalidActionException {
		if (!existingActions.contains(ActionType.CHECK_IN)) {
			throw new InvalidActionException("You haven't checked in today.", LocalizationKey.NO_CHECK_IN.getKey());
		}
	}

	private void validatePauseEnd(Set<ActionType> existingActions) throws InvalidActionException {
		if (existingActions.contains(ActionType.CHECK_OUT)) {
			throw new InvalidActionException("You have checked out and all actions have been closed!", LocalizationKey.ALREADY_CHECKED_OUT.getKey());
		}
		if (!existingActions.contains(ActionType.PAUSE_START)) {
			throw new InvalidActionException("You haven't started pause yet.", LocalizationKey.NO_PAUSE_START.getKey());
		}
	}

	private void validateBusinessEnd(Set<ActionType> existingActions) throws InvalidActionException {
		if (existingActions.contains(ActionType.CHECK_OUT)) {
			throw new InvalidActionException("You have checked out and all actions have been closed!", LocalizationKey.ALREADY_CHECKED_OUT.getKey());
		}
		if (!existingActions.contains(ActionType.BUSINESS_START)) {
			throw new InvalidActionException("You haven't registered a business entry.", LocalizationKey.NO_BUSINESS_START.getKey());
		}
	}

	private void validateStartActions(Set<ActionType> existingActions) throws InvalidActionException {
		if (existingActions.contains(ActionType.CHECK_OUT)) {
			throw new InvalidActionException("You have checked out!", LocalizationKey.ALREADY_CHECKED_OUT.getKey());
		}
		if (!existingActions.contains(ActionType.CHECK_IN)) {
			throw new InvalidActionException("You haven't checked in today.", LocalizationKey.NO_CHECK_IN.getKey());
		}
	}

	private void closeActions(List<Timetable> timetableHistory, Timetable object) {
		Optional<Timetable> timetableCheckIn = timetableHistory.stream().filter(timetable -> timetable.getActionType() == ActionType.CHECK_IN).findFirst();
		if (timetableCheckIn.isEmpty()) return;
		if (hasAction(ActionType.PAUSE_START, timetableHistory) && !hasAction(ActionType.PAUSE_END, timetableHistory)) {
			createAutoAction(ActionType.PAUSE_END, object, timetableCheckIn.get().getGroupingKey());
		}
		if (hasAction(ActionType.BUSINESS_START, timetableHistory) && !hasAction(ActionType.BUSINESS_END, timetableHistory)) {
			createAutoAction(ActionType.BUSINESS_START, object, timetableCheckIn.get().getGroupingKey());
		}
	}

	private void createAutoAction(ActionType actionType, Timetable object, Long groupingKey) {
		Timetable timetable = new Timetable();
		timetable.setUser(object.getUser());
		timetable.setActionType(actionType);
		timetable.setDevice(object.getDevice());
		timetable.setDateTime(object.getDateTime());
		timetable.setShift(object.getShift());
		timetable.setGroupingKey(groupingKey);
		repository.save(timetable);
	}

	public static Timetable getTimetableByAction(List<Timetable> timetables, ActionType actionType) {
		return timetables.stream().filter(e -> e.getActionType() == actionType).findFirst().orElse(null);
	}

	public static boolean hasUnpairedAction(ActionType actionType1, ActionType actionType2, List<Timetable> timetables) {
		List<ActionType> actions = timetables.stream().map(Timetable::getActionType).toList();
		return !actions.contains(actionType1) || !actions.contains(actionType2);
	}

	public static boolean hasAction(ActionType actionType, List<Timetable> timetables) {
		return timetables.stream().anyMatch(e -> e.getActionType() == actionType);
	}

	public ActionCount getActionCount(ZonedDateTime endDate, ZonedDateTime startDate) {
		User user = securityUtils.getCurrentUser();
		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
		return repository.countActionTypesBetweenDates(accessible, startDate.toInstant(), endDate.toInstant());
	}

	public Page<User> findAllForDashboard(Pageable pageable, ZonedDateTime endDate, ZonedDateTime startDate) {
		User user = securityUtils.getCurrentUser();
		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
		return repository.findAllPresentForDashboard(accessible, pageable, startDate.toInstant(), endDate.toInstant());
	}

}
