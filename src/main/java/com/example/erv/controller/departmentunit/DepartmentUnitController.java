package com.example.erv.controller.departmentunit;

import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.service.departmentunit.DepartmentUnitService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/departmentUnit")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentUnitController extends DepartmentUnitControllerBasic {

	public DepartmentUnitController(DepartmentUnitService service) {
		super(service);
    }
}
