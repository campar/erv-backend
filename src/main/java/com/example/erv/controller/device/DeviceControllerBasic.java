package com.example.erv.controller.device;

import com.example.erv.service.device.DeviceService;
import com.example.erv.controller.GenericController;
import com.example.erv.model.Device;
import com.example.erv.exceptions.ErvException;

import com.example.erv.service.permission.Permissions;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;

public class DeviceControllerBasic extends GenericController<Device> {
	protected final DeviceService deviceService;

	public DeviceControllerBasic(DeviceService service) {
		super(service);
		this.deviceService = service;
	}

	@Override
	public ResponseEntity<?> findAll() {
		SecurityUtils.checkAuthority(Permissions.Device.READ);
		return super.findAll();
	}

	@Override
	public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Device.READ);
		return super.searchPost(request);
	}

	@Override
	public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Device.READ);
		return super.searchPageablePost(request);
	}

	@Override
	public ResponseEntity<?> delete(Long id) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Device.DELETE);
		return super.delete(id);
	}

	@Override
	public ResponseEntity<?> getById(Long id) {
		SecurityUtils.checkAuthority(Permissions.Device.READ);
		return super.getById(id);
	}

	@Override
	public ResponseEntity<?> update(@Valid Device object) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Device.UPDATE);
		return super.update(object);
	}

	@Override
	public ResponseEntity<?> insert(@Valid Device object) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Device.CREATE);
		return super.insert(object);
	}


}
