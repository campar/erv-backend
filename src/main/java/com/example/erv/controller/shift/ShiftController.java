package com.example.erv.controller.shift;

import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.service.shift.ShiftService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/shift")
@SecurityRequirement(name = "bearerAuth")
public class ShiftController extends ShiftControllerBasic {

	public ShiftController(ShiftService service) {
		super(service);
	}

}
