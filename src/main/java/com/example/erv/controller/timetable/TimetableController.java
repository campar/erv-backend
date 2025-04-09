package com.example.erv.controller.timetable;

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

import com.example.erv.dto.AttendanceRecordDTO;
import com.example.erv.dto.DashboardData;
import com.example.erv.dto.GenericResponse;
import com.example.erv.dto.report.individual.IndividualReportRequest;
import com.example.erv.dto.report.individual.IndividualReportResponse;
import com.example.erv.dto.report.pause.PauseReportRequest;
import com.example.erv.dto.report.pause.PauseReportResponse;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Card;
import com.example.erv.service.card.CardService;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.timetable.DashboardDataService;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.service.timetable.reports.TimetableIndividualReport;
import com.example.erv.service.timetable.reports.TimetablePauseReport;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/timetable")
@SecurityRequirement(name = "bearerAuth")
public class TimetableController extends TimetableControllerBasic {
	private final DashboardDataService dashboardDataService;
	@Autowired
	private CardService cardService;

	public TimetableController(TimetableService service, DashboardDataService dashboardDataService) {
		super(service);
		this.dashboardDataService = dashboardDataService;
	}

	@PostMapping(value = "/getIndividualReport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getIndividualReport(@RequestBody @Valid IndividualReportRequest request) {
		SecurityUtils.checkAuthority(Permissions.Report.VIEW_INDIVIDUAL_REPORT);
		Page<IndividualReportResponse> response = new TimetableIndividualReport(timetableService).getIndividualReport(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/getIndividualReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> getPDFIndividualReport(@RequestBody @Valid IndividualReportRequest request) {
		SecurityUtils.checkAuthority(Permissions.Report.VIEW_INDIVIDUAL_REPORT);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=individual_report.pdf");
		byte[] pdfBytes = new TimetableIndividualReport(timetableService).getIndividualReportPDF(request);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@PostMapping(value = "/getPauseReport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getPauseReport(@RequestBody @Valid PauseReportRequest request) {
		SecurityUtils.checkAuthority(Permissions.Report.VIEW_PAUSE_REPORT);
		Page<PauseReportResponse> response = new TimetablePauseReport(timetableService).getPauseReport(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/getPauseReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> getPauseReportPDF(@RequestBody @Valid PauseReportRequest request) {
		SecurityUtils.checkAuthority(Permissions.Report.VIEW_PAUSE_REPORT);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=pause_report.pdf");
		byte[] pdfBytes = new TimetablePauseReport(timetableService).getPauseReportPDF(request);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@PostMapping(value = "/getDashboardData", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDashboardData(@RequestBody @Valid SearchDTO request) {
		DashboardData response = dashboardDataService.getDashboardData(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/device/action")
    public ResponseEntity<?> registerAction(@RequestBody AttendanceRecordDTO attendanceRecordDTO) throws ErvException {
        Card card = cardService.findByRfid(attendanceRecordDTO.getRfid());
        cardService.validateCard(card);
        timetableService.registerAction(attendanceRecordDTO, card.getUser());
        return ResponseEntity.ok(GenericResponse.getSuccessfulResponse("Action registered successfully."));
    }
}
