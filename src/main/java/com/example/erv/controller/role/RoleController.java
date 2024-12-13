package com.example.erv.controller.role;

import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.service.role.RoleService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/role")
@SecurityRequirement(name = "bearerAuth")
public class RoleController extends RoleControllerBasic {

	public RoleController(RoleService service) {
		super(service);
	}
}
