package com.example.erv.service.user.reports;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.report.daily.DailyReportRequest;
import com.example.erv.dto.report.daily.DailyReportResponse;
import com.example.erv.model.Shift;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.user.UserDailyReportDTO;
import com.example.erv.service.ReportService;
import com.example.erv.service.user.UserService;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.EntityUtils;

public class UserDailyReport {
    private final UserService userService;

    public UserDailyReport(UserService userService) {
        this.userService = userService;
    }

    public Page<DailyReportResponse> getDailyReport(DailyReportRequest request) {
        Page<User> users = userService.findAllForDailyReport(request, request.createPageable());
        ZonedDateTime from = request.getDateTime().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        ZonedDateTime to = request.getDateTime().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX);
        List<UserDailyReportDTO> data = userService.findReportData(from, to, users.toList(), null, request.getDepartmentUnit());
        return users.map(user -> {
            List<UserDailyReportDTO> dto = data.stream().filter(e -> e.getUser().getId().equals(user.getId())).toList();
            return createResponse(dto, user);
        });
    }

    public byte[] getDailyReportPDF(DailyReportRequest request) {
        ReportService reportService = new ReportService();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dateTime", request.getDateTime());
        return reportService.createPDFReportWithSubReport(getDailyReportPDFData(request), parameters, "DailyReport");
    }
    
    public byte[] getDailyReportCsv(DailyReportRequest request) {
    	ReportService reportService = new ReportService();
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put("dateTime", request.getDateTime());
    	return reportService.createXlsxReportWithSubReport(getDailyReportPDFData(request), parameters, "DailyCsvReport");
    }

    public List<DailyReportResponse> getDailyReportPDFData(DailyReportRequest request) {
        Page<User> users = userService.findAllForDailyReport(request, Pageable.unpaged());
        ZonedDateTime from = request.getDateTime().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId()));
        ZonedDateTime to = request.getDateTime().atStartOfDay(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MAX);
        List<UserDailyReportDTO> data = userService.findReportData(from, to, users.toList(), null, request.getDepartmentUnit());
        return users.map(user -> {
            List<UserDailyReportDTO> dto = data.stream().filter(e -> e.getUser().getId().equals(user.getId())).toList();
            return createResponse(dto, user);
        }).toList();
    }

    private DailyReportResponse createResponse(List<UserDailyReportDTO> dto, User user) {
        List<Timetable> timetables = dto.stream().map(UserDailyReportDTO::getTimetable).sorted(Comparator.comparing(Timetable::getDateTime)).toList();

        if (timetables.isEmpty()) {
            return new DailyReportResponse(user, List.of(), "00:00:00", "00:00:00");
        }

        Duration timeSpent = EntityUtils.calculateDuration(timetables, ActionType.CHECK_IN, ActionType.CHECK_OUT);
        Shift shift = timetables.get(0).getShift();
        Duration overtime = timeSpent != null ? timeSpent.minus(shift.getDuration()) : Duration.ZERO;
        if (overtime.isNegative()) overtime = Duration.ZERO;
        return new DailyReportResponse(user, timetables, AppUtil.durationToString(timeSpent), AppUtil.durationToString(overtime));
    }
}
