package com.example.erv.service.user.reports;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.report.leavingarrival.DateTimetable;
import com.example.erv.dto.report.leavingarrival.LeavingArrivalReportRequest;
import com.example.erv.dto.report.leavingarrival.LeavingArrivalReportResponse;
import com.example.erv.model.Shift;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.service.ReportService;
import com.example.erv.service.shift.ShiftAttribute;
import com.example.erv.service.timetable.TimetableAttribute;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.service.user.UserAttribute;
import com.example.erv.service.user.UserService;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.EntityUtils;

import jakarta.validation.Valid;

public class UserLeavingArrivalReport {
    private final UserService service;
    private final TimetableService timetableService;

    public UserLeavingArrivalReport(UserService service, TimetableService timetableService) {
        this.timetableService = timetableService;
        this.service = service;
    }

    public Page<LeavingArrivalReportResponse> getLeavingArrivalReport(LeavingArrivalReportRequest request) {
        Page<User> data = service.findAll(buildUserSpecification(request).getSpecification(), request.createPageable(), false);
        return data.map(e -> createResponse(e, request));
    }

    public byte[] getLeavingArrivalReportPDF(@Valid LeavingArrivalReportRequest request) {
        ReportService reportService = new ReportService();
        List<User> data = service.findAll(buildUserSpecification(request).getSpecification(), false);
        List<LeavingArrivalReportResponse> reportData = data.stream().map(e -> createResponse(e, request)).toList();
    
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dateFrom", request.getDateFrom());
        parameters.put("dateTo", (request.getDateTo() == null ? ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId())) : request.getDateTo()));

        return reportService.createPDFReportWithSubReports(reportData, parameters, "DelaysDeparturesReport", Arrays.asList("SubReport", "TimetableInfoSubReport", "ShiftsSubReport"));
    }
    public byte[] getLeavingArrivalReportCsv(@Valid LeavingArrivalReportRequest request) {
    	ReportService reportService = new ReportService();
    	List<User> data = service.findAll(buildUserSpecification(request).getSpecification(), false);
    	List<LeavingArrivalReportResponse> reportData = data.stream().map(e -> createResponse(e, request)).toList();
    	
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put("dateFrom", request.getDateFrom());
    	parameters.put("dateTo", (request.getDateTo() == null ? ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId())) : request.getDateTo()));
    	
    	return reportService.createXlsxReportWithSubReports(reportData, parameters, "DelaysDeparturesCsvReport", Arrays.asList("SubReport", "TimetableInfoSubReport", "ShiftsSubReport"));
    }
    
    private LeavingArrivalReportResponse createResponse(User user, LeavingArrivalReportRequest request) {
        LeavingArrivalReportResponse response = new LeavingArrivalReportResponse();
        response.setUser(user);
        response.setDepartmentUnit(user.getDepartmentUnit());
        response.setShifts(user.getShifts().stream().toList());
        LocalDate to = request.getDateTo() == null ? LocalDate.now() : request.getDateTo().toLocalDate();

        Map<LocalDate, List<Timetable>> dayMap = EntityUtils.createTimetableDateRangeMap(request.getDateFrom().toLocalDate(), to);
        Map<LocalDate, List<Timetable>> timetable = EntityUtils.groupByDay(timetableService.findByUserAndShiftAndDateRange(user, request.getShifts(), request.getDateFrom().with(LocalTime.MIN), to.atStartOfDay(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX)));
        timetable = AppUtil.mergeMaps(timetable, dayMap);
        List<DateTimetable> dateTimetables = new ArrayList<>();
        timetable.forEach((key, value) -> {
        	if(!isWeekend(key) || (isWeekend(key) && !CollectionUtils.isEmpty(value))) {
        		dateTimetables.add(createTimetableDataForDay(key, value));
        	}
        });
        response.setDateTimetables(dateTimetables);
        response.setFaultsCount(countFaults(dateTimetables));
        return response;
    }

    private Integer countFaults(List<DateTimetable> dateTimetables) {
        int counter = 0;
        for (DateTimetable dateTimetable : dateTimetables) {
            boolean checkInOut = dateTimetable.getTimetables().stream().filter(e -> e.getTimetable().getActionType() == ActionType.CHECK_IN || e.getTimetable().getActionType() == ActionType.CHECK_OUT).count() == 2;
            if (!checkInOut) counter++;
            for (TimetableActionData timetable : dateTimetable.getTimetables()) {
                if (!timetable.isValid()) counter++;
            }
        }
        return counter;
    }

    private DateTimetable createTimetableDataForDay(LocalDate day, List<Timetable> timetables) {
        DateTimetable dateTimetable = new DateTimetable();
        dateTimetable.setTimetableDate(day);
        dateTimetable.setTimetables(createAndValidateTimetableData(timetables));
        Shift shift = timetables.isEmpty() ? null : timetables.get(0).getShift();
        dateTimetable.setShift(shift);
        return dateTimetable;
    }
    
    private static boolean isWeekend(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private List<TimetableActionData> createAndValidateTimetableData(List<Timetable> timetables) {
        TimetableActionData checkIn = new TimetableActionData(ActionType.CHECK_IN, TimetableService.getTimetableByAction(timetables, ActionType.CHECK_IN));
        TimetableActionData checkOut = new TimetableActionData(ActionType.CHECK_OUT, TimetableService.getTimetableByAction(timetables, ActionType.CHECK_OUT));
        TimetableActionData pauseStart = new TimetableActionData(ActionType.PAUSE_START, TimetableService.getTimetableByAction(timetables, ActionType.PAUSE_START));
        TimetableActionData pauseEnd = new TimetableActionData(ActionType.PAUSE_END, TimetableService.getTimetableByAction(timetables, ActionType.PAUSE_END));
        TimetableActionData businessIn = new TimetableActionData(ActionType.BUSINESS_START, TimetableService.getTimetableByAction(timetables, ActionType.BUSINESS_START));
        TimetableActionData businessOut = new TimetableActionData(ActionType.BUSINESS_END, TimetableService.getTimetableByAction(timetables, ActionType.BUSINESS_END));
        
        List<TimetableActionData> data = List.of(checkIn, checkOut, pauseStart, pauseEnd, businessIn, businessOut);
        
        if (isPauseActionInvalid(timetables, pauseStart, pauseEnd)) {
            pauseStart.setValid(false);
            pauseEnd.setValid(false);
        }

        if (isBusinessActionInvalid(timetables, businessIn, businessOut)) {
            businessIn.setValid(false);
            businessOut.setValid(false);
        }
        if (TimetableService.hasAction(ActionType.CHECK_IN, timetables) && isCheckInLate(timetables)) {
            checkIn.setValid(false);
        }
        if (TimetableService.hasAction(ActionType.CHECK_OUT, timetables) && isCheckOutEarly(timetables)) {
            checkOut.setValid(false);
        }

        // We filter out the items that don't contain any real records in the timetable table.
        return new ArrayList<>(data).stream().filter(e -> e.getTimetable() != null).sorted(Comparator.comparing(t -> t.getTimetable().getDateTime())).toList();
    }

    private static boolean isPauseActionInvalid(List<Timetable> timetables, TimetableActionData pauseStart, TimetableActionData pauseEnd) {
        return TimetableService.hasUnpairedAction(ActionType.PAUSE_START, ActionType.PAUSE_END, timetables)
                && (pauseStart.getTimetable() != null || pauseEnd.getTimetable() != null);
    }

    private static boolean isBusinessActionInvalid(List<Timetable> timetables, TimetableActionData businessIn, TimetableActionData businessOut) {
        return TimetableService.hasUnpairedAction(ActionType.BUSINESS_START, ActionType.BUSINESS_END, timetables)
                && (businessIn.getTimetable() != null || businessOut.getTimetable() != null);
    }

    /**
     * Checks if the time spent is less than the shift duration or<br>
     * if employee has checked out before the end of the shift set on the timetable object.
     */
    private boolean isCheckOutEarly(List<Timetable> timetables) {
        Timetable checkOut = TimetableService.getTimetableByAction(timetables, ActionType.CHECK_OUT);
        Timetable checkIn = TimetableService.getTimetableByAction(timetables, ActionType.CHECK_IN);
        LocalTime checkOutTime = checkOut.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).toLocalTime();
        Shift shift = checkOut.getShift();
        LocalTime checkOutFrom = LocalTime.parse(shift.getEndFrom());
        return checkIn.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).toLocalTime().until(checkOutTime, ChronoUnit.MINUTES) < shift.getFullDurationInMinutes()
                || checkOutTime.isBefore(checkOutFrom);
    }

    /**
     * @param timetables Timetable records for one logical set (e.g. timetables by day)
     * @return if check in time is after upper bound of check in sliding window.
     */
    private boolean isCheckInLate(List<Timetable> timetables) {
        Timetable timetable = TimetableService.getTimetableByAction(timetables, ActionType.CHECK_IN);
        LocalTime checkInTime = timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).toLocalTime();
        Shift shift = timetable.getShift();
        LocalTime checkInTo = LocalTime.parse(shift.getStartTo());
        return checkInTime.isAfter(checkInTo);
    }

    /**
     * Specification which is being prepared for gathering users for this report.<br>
     * Data should be distinct (because of join with the timetable table).<br>
     * <strong>Mandatory conditions:</strong><br>
     * &#9;- lower bound datetime<br>
     * <strong>Optional conditions:</strong><br>
     * &#9;- department unit,<br>
     * &#9;- upper bound datetime for timetable inputs (defaults to current day's maximum),<br>
     * &#9;- list of selected shifts that are contained in timetables for search<br>
     * <br>
     * <strong>Data will be additionally filtered when this specification is passed to findAll method to close the set of data within all allowed department units.</strong>
     */
    public SpecificationBasic<User> buildUserSpecification(LeavingArrivalReportRequest request) {
        SpecificationBasic<User> spec = SpecificationBasic.<User>get().distinct();
        if (request.getDepartmentUnit() != null && !request.getDepartmentUnit().isEmpty()) {
    		spec = spec.and(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, request.getDepartmentUnit());
        }
        if (request.getShifts() != null && !request.getShifts().isEmpty()) {
        	SpecificationBasic<Shift> shiftSpec = SpecificationBasic.<Shift>get();
    		shiftSpec.or(ShiftAttribute.ID, SearchOperator.EQUAL, request.getShifts().stream().map(Shift::getId).toList());
        	spec = spec.joinAnd(UserAttribute.SHIFTS, shiftSpec);
        }
        
        List<ZonedDateTime> range = new ArrayList<>();
        range.add(request.getDateFrom());
        range.add(request.getDateTo() == null ? ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX) : request.getDateTo());
        SpecificationBasic<Timetable> timetableSpec = SpecificationBasic.where(TimetableAttribute.DATE_TIME, SearchOperator.BETWEEN, range);
        if (request.getShifts() != null && !request.getShifts().isEmpty())
            timetableSpec = timetableSpec.and(TimetableAttribute.SHIFT, SearchOperator.IN, request.getShifts());
        
    	// Add soft-delete filtering:
        // Include:
        // - Non-soft-deleted users OR
        // - Soft-deleted users modified after report start date
        SpecificationBasic<User> softDeleteSpec = SpecificationBasic
                .<User>where(UserAttribute.SOFT_DELETED, SearchOperator.EQUAL, false)
                .or(
                    SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, SearchOperator.EQUAL, true)
                        .and(UserAttribute.MODIFY_DATE, SearchOperator.GTE, request.getDateFrom())
                );
        
        spec = spec.joinAnd(UserAttribute.TIMETABLE_LIST, timetableSpec).and(softDeleteSpec);
        return spec;
    }
}
