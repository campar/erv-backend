package com.example.erv.service.timetable.reports;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.report.pause.PauseReportRequest;
import com.example.erv.dto.report.pause.PauseReportResponse;
import com.example.erv.dto.report.pause.TimetableInfo;
import com.example.erv.model.Timetable;
import com.example.erv.model.enums.ActionType;
import com.example.erv.service.ReportService;
import com.example.erv.service.timetable.TimetableAttribute;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.EntityUtils;

public class TimetablePauseReport {
    private final TimetableService timetableService;

    public TimetablePauseReport(TimetableService service) {
        this.timetableService = service;
    }

    public Page<PauseReportResponse> getPauseReport(PauseReportRequest request) {
        Page<LocalDate> dates = timetableService.findReportDatesByRangeAndUser(request.getDateFrom(), request.getDateTo(), request.getUser().getId(), List.of(ActionType.PAUSE_START), request.createPageable());
        List<Timetable> data = timetableService.findByDates(dates.toList(), request.getUser(), List.of(ActionType.CHECK_IN, ActionType.PAUSE_START, ActionType.PAUSE_END));

        List<PauseReportResponse> responseBuffer = groupByDay(data);
        Pageable pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize());
        return new PageImpl<>(responseBuffer, pageRequest, dates.getTotalElements());
    }

    private List<PauseReportResponse> groupByDay(List<Timetable> data) {
        Map<LocalDate, List<Timetable>> byDay = EntityUtils.groupByDay(data);
        List<PauseReportResponse> responseBuffer = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Timetable>> entry : byDay.entrySet()) {
            Timetable checkIn = entry.getValue().stream().filter(e -> e.getActionType() == ActionType.CHECK_IN).findAny().orElseThrow();
            List<Timetable> pauseActions = entry.getValue().stream().filter(e -> e != checkIn).toList();
            responseBuffer.add(createResponse(pauseActions, checkIn.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).toLocalDate()));
        }
        responseBuffer.sort(Comparator.comparing(PauseReportResponse::getDate).reversed());
        return responseBuffer;
    }

    public byte[] getPauseReportPDF(PauseReportRequest request) {
        ReportService reportService = new ReportService();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dateFrom", request.getDateFrom());
        parameters.put("dateTo", request.getDateTo());
        parameters.put("user", request.getUser());
        return reportService.createPDFReportWithSubReport(getPauseReportPDFData(request), parameters, "PauseReport");
    }

    private List<PauseReportResponse> getPauseReportPDFData(PauseReportRequest request) {
    	 Page<LocalDate> dates = timetableService.findReportDatesByRangeAndUser(request.getDateFrom(), request.getDateTo(), request.getUser().getId(), List.of(ActionType.PAUSE_START), Pageable.unpaged());
         List<Timetable> data = timetableService.findByDates(dates.toList(), request.getUser(), List.of(ActionType.CHECK_IN, ActionType.PAUSE_START, ActionType.PAUSE_END));

        return groupByDay(data);
    }

    private PauseReportResponse createResponse(List<Timetable> timetables, LocalDate actionDate) {
        PauseReportResponse response = new PauseReportResponse();
        response.setDate(actionDate);
        response.setTimetableInfo(
                timetables.stream().map(e -> new TimetableInfo(
                        e.getActionType(),
                        e.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())),
                        e.getDevice() == null ? null : e.getDevice().getLocation(),
                        e.getDevice())).sorted(Comparator.comparing(TimetableInfo::getTime)).toList());
        Duration duration = EntityUtils.calculateDuration(timetables, ActionType.PAUSE_START, ActionType.PAUSE_END);
        response.setDuration(AppUtil.durationToString(duration));
        return response;
    }

    public SpecificationBasic<Timetable> buildSpecification(PauseReportRequest request) {
        return SpecificationBasic
                .<Timetable>where(TimetableAttribute.DATE_TIME, SearchOperator.GTE, request.getDateFrom())
                .and(TimetableAttribute.DATE_TIME, SearchOperator.LTE, request.getDateTo())
                .and(TimetableAttribute.USER, request.getUser())
                .and(SpecificationBasic
                        .<Timetable>where(TimetableAttribute.ACTION_TYPE, ActionType.PAUSE_START)
                        .or(TimetableAttribute.ACTION_TYPE, ActionType.PAUSE_END));
    }
}
