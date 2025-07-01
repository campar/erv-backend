package com.example.erv.controller.external;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.dto.AttendanceRecordDTO;
import com.example.erv.dto.GenericResponse;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Card;
import com.example.erv.service.card.CardService;
import com.example.erv.service.device.DeviceService;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.utils.AppUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/api/external")
public class AttendanceController {
	private static final Logger logger = LogManager.getLogger(CardService.class);
	
    private final CardService cardService;
    private final TimetableService timetableService;
    private final DeviceService deviceService;

    public AttendanceController(CardService cardService, TimetableService timetableService, DeviceService deviceService) {
        super();
        this.cardService = cardService;
        this.timetableService = timetableService;
        this.deviceService = deviceService;
    }

    @PostMapping("/device/action")
    public ResponseEntity<?> registerAction(@RequestBody AttendanceRecordDTO attendanceRecordDTO) throws ErvException {
    	logger.info("AttendanceController.registerAction: attendance request: {}", attendanceRecordDTO);
        Card card = cardService.findByRfid(attendanceRecordDTO.getRfid());
        cardService.validateCard(card);
        timetableService.registerAction(attendanceRecordDTO, card.getUser());
        return ResponseEntity.ok(GenericResponse.getSuccessfulResponse("Action registered successfully."));
    }

    @GetMapping({"/ping", "/ping/{hardwareIdentifier}"})
    public ResponseEntity<?> ping(@PathVariable(value = "hardwareIdentifier", required = false) String hardwareId, HttpServletRequest request) {
    	if (!StringUtils.isBlank(hardwareId)) {
    		String ipAddress = AppUtil.getClientIpAddress(request);
        	deviceService.recordHeartbeat(hardwareId, ipAddress);
    	}
        return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(""));
    }
}
