package com.example.erv.service.device.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.erv.dto.report.checkinstats.CheckIn;
import com.example.erv.dto.report.checkinstats.CheckInStatsReportRequest;
import com.example.erv.dto.report.checkinstats.CheckInStatsReportResponse;
import com.example.erv.model.Device;
import com.example.erv.repository.timetable.CheckInStatsDTO;
import com.example.erv.service.ReportService;
import com.example.erv.service.device.DeviceService;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.specification.SpecificationBasic;

@Service
public class DeviceCheckInStatsReport {
    private final DeviceService deviceService;
    private final TimetableService timetableService;

    public DeviceCheckInStatsReport(DeviceService service, TimetableService timetableService) {
        this.deviceService = service;
        this.timetableService = timetableService;
    }

    public Page<CheckInStatsReportResponse> getCheckInStatsReport(CheckInStatsReportRequest request) {
        Page<Device> data = deviceService.findAll(SpecificationBasic.<Device>get().getSpecification(), request.createPageable());
        List<CheckInStatsDTO> timetableData = timetableService.findCheckInStats(data.map(Device::getId).toList(), request.getDateFrom(), request.getDateTo());
        return data.map(e -> createResponse(e, timetableData.stream().filter(byDevice -> byDevice.getId().equals(e.getId())).toList()));
    }

    public byte[] getCheckInStatsReportPDF(CheckInStatsReportRequest request) {
        ReportService reportService = new ReportService();
        List<Device> data = deviceService.findAll(SpecificationBasic.<Device>get().getSpecification());
        List<CheckInStatsDTO> timetableData = timetableService.findCheckInStats(data.stream().map(Device::getId).toList(), request.getDateFrom(), request.getDateTo());
        List<?> reportData = data.stream().map(e -> createResponse(e, timetableData.stream().filter(byDevice -> byDevice.getId().equals(e.getId())).toList())).toList();
    	Map<String, Object> parameters = new HashMap<>();
    	parameters.put("dateFrom", request.getDateFrom());
    	parameters.put("dateTo", request.getDateTo());
	    return reportService.createPDFReportWithSubReport(reportData, parameters, "PassStatisticsReport");
    }

    private CheckInStatsReportResponse createResponse(Device device, List<CheckInStatsDTO> timetableData) {
        CheckInStatsReportResponse response = new CheckInStatsReportResponse();
        response.setDevice(device);
        response.setCheckIns(timetableData.stream().map(e -> new CheckIn(e.getActionType(), e.getCount())).toList());
        response.setTotal(timetableData.stream().mapToLong(CheckInStatsDTO::getCount).sum());
        return response;
    }
}
