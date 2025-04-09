package com.example.erv.controller.timetable;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Timetable;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class TimetableControllerBasic extends GenericController<Timetable> {
	protected final TimetableService timetableService;

	public TimetableControllerBasic(TimetableService service) {
		super(service);
		this.timetableService = service;
	}

	@Override
	public ResponseEntity<?> findAll() {
		SecurityUtils.checkAuthority(Permissions.Timetable.READ);
		return super.findAll();
	}

	@Override
	public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Timetable.READ);
		return super.searchPost(request);
	}

	@Override
	public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Timetable.READ);
		return super.searchPageablePost(request);
	}

	@Override
	public ResponseEntity<?> delete(Long id) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Timetable.DELETE);
		return super.delete(id);
	}

	@Override
	public ResponseEntity<?> getById(Long id) {
		SecurityUtils.checkAuthority(Permissions.Timetable.READ);
		return super.getById(id);
	}

	@Override
	public ResponseEntity<?> update(@Valid Timetable object) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Timetable.UPDATE);
		return super.update(object);
	}

	@Override
	public ResponseEntity<?> insert(@Valid Timetable object) throws ErvException {
		SecurityUtils.checkAuthority(Permissions.Timetable.CREATE);
		return super.insert(object);
	}


}
