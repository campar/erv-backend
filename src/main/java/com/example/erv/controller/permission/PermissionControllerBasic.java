package com.example.erv.controller.permission;

import com.example.erv.service.permission.PermissionService;
import com.example.erv.controller.GenericController;
import com.example.erv.model.Permission;
import com.example.erv.exceptions.ErvException;

import com.example.erv.service.permission.Permissions;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;

public class PermissionControllerBasic extends GenericController<Permission> {
	protected final PermissionService permissionService;

	public PermissionControllerBasic(PermissionService service) {
		super(service);
		this.permissionService = service;
	}

	@Override
	public ResponseEntity<?> findAll() {
		SecurityUtils.checkAuthority(Permissions.Permission.READ);
		return super.findAll();
	}

	@Override
	public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Permission.READ);
		return super.searchPost(request);
	}

	@Override
	public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Permission.READ);
		return super.searchPageablePost(request);
	}

	@Override
	public ResponseEntity<?> delete(Long id) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Permission.DELETE);
		return super.delete(id);
	}

	@Override
	public ResponseEntity<?> getById(Long id) {
		SecurityUtils.checkAuthority(Permissions.Permission.READ);
		return super.getById(id);
	}

	@Override
	public ResponseEntity<?> update(@Valid Permission object) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Permission.UPDATE);
		return super.update(object);
	}

	@Override
	public ResponseEntity<?> insert(@Valid Permission object) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Permission.CREATE);
		return super.insert(object);
	}


}
