package com.example.erv.controller.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.dto.report.checkinstats.CheckInStatsReportRequest;
import com.example.erv.dto.report.checkinstats.CheckInStatsReportResponse;
import com.example.erv.service.device.DeviceService;
import com.example.erv.service.device.reports.DeviceCheckInStatsReport;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/device")
@SecurityRequirement(name = "bearerAuth")
public class DeviceController extends DeviceControllerBasic {
    @Autowired
    private TimetableService timetableService;

    public DeviceController(DeviceService service) {
        super(service);
    }

    @PostMapping(value = "/getCheckInStatsReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCheckInStatsReport(@RequestBody @Valid CheckInStatsReportRequest request) {
		SecurityUtils.checkAuthority(Permissions.Report.VIEW_CHECK_IN_CHECK_OUT_STATUS_REPORT);
        Page<CheckInStatsReportResponse> response = new DeviceCheckInStatsReport(deviceService, timetableService).getCheckInStatsReport(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/getCheckInStatsReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getCheckInStatsReportPDF(@RequestBody @Valid CheckInStatsReportRequest request) {
    	SecurityUtils.checkAuthority(Permissions.Report.VIEW_CHECK_IN_CHECK_OUT_STATUS_REPORT);
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=checkin_status_report.pdf");
	    byte[] pdfBytes = new DeviceCheckInStatsReport(deviceService, timetableService).getCheckInStatsReportPDF(request);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }
    
}
