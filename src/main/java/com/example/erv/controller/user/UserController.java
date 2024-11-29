package com.example.erv.controller.user;

import com.example.erv.dataimport.UserCsvImportService;
import com.example.erv.dto.GenericResponse;
import com.example.erv.dto.UserCsvDTO;
import com.example.erv.dto.report.absence.AbsenceReportRequest;
import com.example.erv.dto.report.absence.AbsenceReportResponse;
import com.example.erv.dto.report.daily.DailyReportRequest;
import com.example.erv.dto.report.daily.DailyReportResponse;
import com.example.erv.dto.report.leavingarrival.LeavingArrivalReportRequest;
import com.example.erv.dto.report.leavingarrival.LeavingArrivalReportResponse;
import com.example.erv.dto.report.workinghours.WorkingHoursReportRequest;
import com.example.erv.dto.report.workinghours.WorkingHoursReportResponse;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.User;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.service.user.UserService;
import com.example.erv.service.user.reports.UserAbsenceReport;
import com.example.erv.service.user.reports.UserDailyReport;
import com.example.erv.service.user.reports.UserLeavingArrivalReport;
import com.example.erv.service.user.reports.UserWorkingHoursReport;
import com.example.erv.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = "bearerAuth")
public class UserController extends UserControllerBasic {
    @Autowired
    UserCsvImportService userImportService;

    @Autowired
    SecurityUtils securityUtils;

    private final TimetableService timetableService;

    public UserController(UserService service, TimetableService timetableService) {
        super(service);
        this.timetableService = timetableService;
    }

    @PostMapping(value = "/getAbsenceReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAbsenceReport(@RequestBody @Valid AbsenceReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_ATTENDANCE_REPORT);
        Page<AbsenceReportResponse> response = new UserAbsenceReport(userService).getAbsenceReport(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/getAbsenceReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAbsenceReportPDF(@RequestBody @Valid AbsenceReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_ATTENDANCE_REPORT);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=pressence_absence_report.pdf");
        byte[] pdfBytes = new UserAbsenceReport(userService).getAbsenceReportPDF(request);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }

    @PostMapping(value = "/getAbsenceReportCsv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getAbsenceReportCsv(@RequestBody @Valid AbsenceReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_ATTENDANCE_REPORT);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=pressence_absence_report.csv");
        byte[] csvBytes = new UserAbsenceReport(userService).getAbsenceReportCsv(request);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(csvBytes);
    }

    @PostMapping(value = "/getWorkingHoursReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWorkingHoursReport(@RequestBody @Valid WorkingHoursReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_WORKING_HOURS_CALCULATION_REPORT);
        Page<WorkingHoursReportResponse> response = new UserWorkingHoursReport(userService).getWorkingHoursReport(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/getWorkingHoursReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getWorkingHoursReportPDF(@RequestBody @Valid WorkingHoursReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_WORKING_HOURS_CALCULATION_REPORT);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=working_hour_report.pdf");
        byte[] pdfBytes = new UserWorkingHoursReport(userService).getWorkingHoursReportPDF(request);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }

    @PostMapping(value = "/getWorkingHoursReportCsv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getWorkingHoursReportCsv(@RequestBody @Valid WorkingHoursReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_WORKING_HOURS_CALCULATION_REPORT);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=working_hour_report.csv");
        byte[] csvBytes = new UserWorkingHoursReport(userService).getWorkingHoursReportCsv(request);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(csvBytes);
    }

    @PostMapping(value = "/getLeavingArrivalReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLeavingArrivalReport(@RequestBody @Valid LeavingArrivalReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_LEAVING_ARRIVAL_REPORT);
        Page<LeavingArrivalReportResponse> response = new UserLeavingArrivalReport(userService, timetableService).getLeavingArrivalReport(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/getLeavingArrivalReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getLeavingArrivalReportPDF(@RequestBody @Valid LeavingArrivalReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_LEAVING_ARRIVAL_REPORT);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=leaving_arrival_report.pdf");
        byte[] pdfBytes = new UserLeavingArrivalReport(userService, timetableService).getLeavingArrivalReportPDF(request);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }
    
    @PostMapping(value = "/getLeavingArrivalReportCsv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getLeavingArrivalReportCsv(@RequestBody @Valid LeavingArrivalReportRequest request) {
    	SecurityUtils.checkAuthority(Permissions.Report.VIEW_LEAVING_ARRIVAL_REPORT);
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-Disposition", "inline; filename=leaving_arrival_report.xlsx");
    	byte[] xlsxBytes = new UserLeavingArrivalReport(userService, timetableService).getLeavingArrivalReportCsv(request);
    	return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(xlsxBytes);
    }

    @PostMapping(value = "/getDailyReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDailyReport(@RequestBody @Valid DailyReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_DAILY_REPORT);
        Page<DailyReportResponse> page = new UserDailyReport(userService).getDailyReport(request);
        return ResponseEntity.ok(page);
    }

    @PostMapping(value = "/getDailyReportPDF", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getDailyReportPDF(@RequestBody @Valid DailyReportRequest request) {
        SecurityUtils.checkAuthority(Permissions.Report.VIEW_DAILY_REPORT);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=daily_report.pdf");
        byte[] pdfBytes = new UserDailyReport(userService).getDailyReportPDF(request);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }
    
    @PostMapping(value = "/getDailyReportCsv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getDailyReportCsv(@RequestBody @Valid DailyReportRequest request) {
    	SecurityUtils.checkAuthority(Permissions.Report.VIEW_DAILY_REPORT);
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Content-Disposition", "inline; filename=daily_report.xlsx");
    	byte[] csvBytes = new UserDailyReport(userService).getDailyReportCsv(request);
    	return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(csvBytes);
    }
    
    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) throws IOException, ErvException {
        SecurityUtils.checkAuthority(Permissions.User.IMPORT);
        List<UserCsvDTO> raw = userImportService.parseCsv(file);
        userService.importUsers(raw);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePassword(@RequestBody @Valid User user) {
        SecurityUtils.checkAuthority(Permissions.User.UPDATE_PASSWORD);
        try {
            return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(userService.updatePassword(user)));
        } catch (ErvException e) {
            return ResponseEntity.status(400).body(GenericResponse.getErrorResponse("400", "Cannot change password."));
        }
    }

    @Override
    public ResponseEntity<?> update(@Valid User object) throws ErvException {
        if (securityUtils.getCurrentUser().getId().equals(object.getId())) {
            throw new ErvException("Current user cannot change itself!");
        }
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        if (securityUtils.getCurrentUser().getId().equals(id)) {
            throw new ErvException("Current user cannot delete itself!");
        }
        return super.delete(id);
    }
}
