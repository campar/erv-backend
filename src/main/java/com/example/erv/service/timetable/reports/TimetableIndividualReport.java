package com.example.erv.service.timetable.reports;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.report.individual.IndividualReportRequest;
import com.example.erv.dto.report.individual.IndividualReportResponse;
import com.example.erv.dto.report.individual.TimetableInfo;
import com.example.erv.model.Timetable;
import com.example.erv.model.enums.ActionType;
import com.example.erv.service.ReportService;
import com.example.erv.service.timetable.TimetableAttribute;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.EntityUtils;


public class TimetableIndividualReport {
    private final TimetableService timetableService;

    public TimetableIndividualReport(TimetableService service) {
        this.timetableService = service;
    }

    public Page<IndividualReportResponse> getIndividualReport(IndividualReportRequest request) {
        List<ActionType> actions = Arrays.stream(ActionType.values()).toList();
        Page<LocalDate> dates = timetableService.findReportDatesByRangeAndUser(request.getDateFrom(), request.getDateTo(), request.getUser().getId(), actions, request.createPageable());
        List<Timetable> data = timetableService.findByDates(dates.toList(), request.getUser(), actions);
        List<IndividualReportResponse> responseBuffer = groupByDay(data);
        Pageable pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize());
        return new PageImpl<>(responseBuffer, pageRequest, dates.getTotalElements());
    }

    private List<IndividualReportResponse> groupByDay(List<Timetable> data) {
        Map<LocalDate, List<Timetable>> byDay = EntityUtils.groupByDay(data);
        List<IndividualReportResponse> responseBuffer = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Timetable>> entry : byDay.entrySet()) {
            responseBuffer.add(createResponse(entry.getValue()));
        }
        return responseBuffer;
    }

    public byte[] getIndividualReportPDF(IndividualReportRequest request) {
        ReportService reportService = new ReportService();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dateFrom", request.getDateFrom());
        parameters.put("dateTo", request.getDateTo());
        parameters.put("user", request.getUser());
        return reportService.createPDFReportWithSubReport(getIndividualReportPDFData(request), parameters, "IndividualReport");
    }

    public List<IndividualReportResponse> getIndividualReportPDFData(IndividualReportRequest request) {
        List<Timetable> data = timetableService.findAll(buildSpecification(request).getSpecification());
        return groupByDay(data);
    }

    private IndividualReportResponse createResponse(List<Timetable> timetables) {
        IndividualReportResponse response = new IndividualReportResponse();
        response.setDate(timetables.get(0).getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).toLocalDate());
        response.setTimetableInfo(
                timetables.stream().map(e -> new TimetableInfo(
                        e.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())),
                        e.getActionType(),
                        e.getDevice() == null ? null : e.getDevice().getLocation(),
                        e.getDevice())).sorted(Comparator.comparing(TimetableInfo::getTime)).toList());
        Duration duration = EntityUtils.calculateDuration(timetables, ActionType.CHECK_IN, ActionType.CHECK_OUT);
        response.setTimeSpent(AppUtil.durationToString(duration));
        return response;
    }

    public SpecificationBasic<Timetable> buildSpecification(IndividualReportRequest request) {
        return SpecificationBasic.<Timetable>where(TimetableAttribute.USER, request.getUser())
                .and(TimetableAttribute.DATE_TIME, SearchOperator.GTE, request.getDateFrom())
                .and(TimetableAttribute.DATE_TIME, SearchOperator.LTE, request.getDateTo());
    }
}
