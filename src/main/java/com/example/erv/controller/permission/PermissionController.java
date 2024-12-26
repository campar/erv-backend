package com.example.erv.controller.permission;

import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Permission;
import com.example.erv.service.permission.PermissionService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/permission")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController extends PermissionControllerBasic {

	public PermissionController(PermissionService service) {
		super(service);
	}
	
	@Override
	@Deprecated
	@Hidden
	@RequestMapping(value = "unavailable", method = RequestMethod.POST)
	public ResponseEntity<?> insert(@Valid Permission object) throws ErvException {
		throw new UnsupportedOperationException("This endpoint is disabled.");
	}
	
	@Override
	@Deprecated
	@Hidden
	@RequestMapping(value = "unavailable", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@Valid Permission object) throws ErvException {
		throw new UnsupportedOperationException("This endpoint is disabled.");
	}
	
	@Override
	@Deprecated
	@Hidden
	@RequestMapping(value = "unavailable", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(Long id) throws ErvException {
		throw new UnsupportedOperationException("This endpoint is disabled.");
	}

}
