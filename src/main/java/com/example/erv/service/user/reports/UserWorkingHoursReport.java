package com.example.erv.service.user.reports;

import com.example.erv.dto.report.workinghours.WorkingHoursReportRequest;
import com.example.erv.dto.report.workinghours.WorkingHoursReportResponse;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.user.UserDailyReportDTO;
import com.example.erv.service.ReportService;
import com.example.erv.service.user.UserService;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.EntityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserWorkingHoursReport {
    private final UserService userService;

    public UserWorkingHoursReport(UserService service) {
        this.userService = service;
    }

    public Page<WorkingHoursReportResponse> getWorkingHoursReport(WorkingHoursReportRequest request) {
        Page<User> users = userService.findAllWorkingHoursReport(request, request.createPageable());
        List<UserDailyReportDTO> data = userService.findReportData(request.getDateFrom(), request.getDateTo(), users.toList(), null, request.getDepartmentUnit());
        return buildResponse(users, data);
    }

    private Page<WorkingHoursReportResponse> buildResponse(Page<User> users, List<UserDailyReportDTO> data) {
        return users.map(user -> {
            List<UserDailyReportDTO> dto = data.stream().filter(e -> e.getUser().getId().equals(user.getId())).toList();
            return createResponse(dto, user);
        });
    }

    public byte[] getWorkingHoursReportPDF(@Valid WorkingHoursReportRequest request) {
        ReportService reportService = new ReportService();

        // Pageable.unpaged() returns all without pagination.
        Page<User> users = userService.findAllWorkingHoursReport(request, Pageable.unpaged());
        List<UserDailyReportDTO> data = userService.findReportData(request.getDateFrom(), request.getDateTo(), users.toList(), null, request.getDepartmentUnit());
        List<WorkingHoursReportResponse> reportData = buildResponse(users, data).toList();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dateFrom", request.getDateFrom());
        parameters.put("dateTo", request.getDateTo());

        return reportService.createPDFReport(reportData, parameters, "WorkHourCalculationReport");
    }
    
    public byte[] getWorkingHoursReportCsv(@Valid WorkingHoursReportRequest request) {
    	ReportService reportService = new ReportService();
    	
    	// Pageable.unpaged() returns all without pagination.
    	Page<User> users = userService.findAllWorkingHoursReport(request, Pageable.unpaged());
    	List<UserDailyReportDTO> data = userService.findReportData(request.getDateFrom(), request.getDateTo(), users.toList(), null, request.getDepartmentUnit());
    	List<WorkingHoursReportResponse> reportData = buildResponse(users, data).toList();
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put("dateFrom", request.getDateFrom());
    	parameters.put("dateTo", request.getDateTo());
    	return reportService.createCsvReport(reportData, parameters, "WorkHourCalculationCsvReport");
    }
	
    private WorkingHoursReportResponse createResponse(List<UserDailyReportDTO> dto, User user) {
        List<Timetable> timetables = dto.stream().map(UserDailyReportDTO::getTimetable).toList();
        Map<LocalDate, List<Timetable>> groupByDay = EntityUtils.groupByDay(timetables);
        Duration total = Duration.ZERO;
        for (List<Timetable> day : groupByDay.values()) {
            Duration duration = EntityUtils.calculateDuration(day, ActionType.CHECK_IN, ActionType.CHECK_OUT);
            if (duration != null)
                total = total.plus(duration);
        }
        return new WorkingHoursReportResponse(user, AppUtil.durationToString(total));
    }
}
