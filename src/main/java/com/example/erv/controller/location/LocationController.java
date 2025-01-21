package com.example.erv.controller.location;

import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.service.location.LocationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/location")
@SecurityRequirement(name = "bearerAuth")
public class LocationController extends LocationControllerBasic {

	public LocationController(LocationService service) {
		super(service);
	}
	

}
