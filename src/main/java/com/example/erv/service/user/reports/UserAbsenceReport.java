package com.example.erv.service.user.reports;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.report.absence.AbsenceReportRequest;
import com.example.erv.dto.report.absence.AbsenceReportResponse;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.user.UserDailyReportDTO;
import com.example.erv.service.ReportService;
import com.example.erv.service.user.UserService;

public class UserAbsenceReport {
    private final UserService userService;

    public UserAbsenceReport(UserService service) {
        this.userService = service;
    }

    public Page<AbsenceReportResponse> getAbsenceReport(AbsenceReportRequest request) {
        Page<User> users = userService.findAllForAbsenceReport(request, request.createPageable());
        ZonedDateTime from = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        ZonedDateTime to = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX);
        List<UserDailyReportDTO> data = userService.findReportData(from, to, users.toList(), null, request.getDepartmentUnit());
        return buildResponse(users, data);
    }

    private Page<AbsenceReportResponse> buildResponse(Page<User> userIds, List<UserDailyReportDTO> data) {
        return userIds.map(user -> {
            List<UserDailyReportDTO> dto = data.stream().filter(e -> e.getUser().getId().equals(user.getId())).toList();
            return createResponse(dto, user);
        });
    }

    public byte[] getAbsenceReportPDF(AbsenceReportRequest request) {
        ReportService reportService = new ReportService();
        // Pageable.unpaged() returns all without pagination.
        Page<User> users = userService.findAllForAbsenceReport(request, Pageable.unpaged());
        ZonedDateTime from = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        ZonedDateTime to = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX);
        List<UserDailyReportDTO> data = userService.findReportData(from, to, users.toList(), null, request.getDepartmentUnit());
        List<AbsenceReportResponse> reportData = buildResponse(users, data).toList();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", request.getDate());
        return reportService.createPDFReport(reportData, parameters, "PressenceAbsenceReport");
    }
	
    public byte[] getAbsenceReportCsv(AbsenceReportRequest request) {
    	ReportService reportService = new ReportService();
    	// Pageable.unpaged() returns all without pagination.
    	Page<User> users = userService.findAllForAbsenceReport(request, Pageable.unpaged());
    	ZonedDateTime from = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        ZonedDateTime to = request.getDate().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX);
    	List<UserDailyReportDTO> data = userService.findReportData(from, to, users.toList(), null, request.getDepartmentUnit());
    	List<AbsenceReportResponse> reportData = buildResponse(users, data).toList();
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put("date", request.getDate());
    	return reportService.createCsvReport(reportData, parameters, "PressenceAbsenceCsvReport");
    }
	
    private AbsenceReportResponse createResponse(List<UserDailyReportDTO> dto, User user) {
        List<Timetable> timetables = dto.stream().map(UserDailyReportDTO::getTimetable).toList();
        return new AbsenceReportResponse(user, user.getDepartmentUnit(), timetables.stream().anyMatch(e -> e.getActionType() == ActionType.CHECK_IN));
    }
}
