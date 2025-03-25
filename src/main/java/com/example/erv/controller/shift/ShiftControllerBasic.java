package com.example.erv.controller.shift;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Shift;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.shift.ShiftService;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class ShiftControllerBasic extends GenericController<Shift> {
    protected final ShiftService shiftService;

    public ShiftControllerBasic(ShiftService service) {
        super(service);
        this.shiftService = service;
    }

    @Override
    public ResponseEntity<?> findAll() {
        SecurityUtils.checkAuthority(Permissions.Shift.READ);
        return super.findAll();
    }

    @Override
    public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Shift.READ);
        return super.searchPost(request);
    }

    @Override
    public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Shift.READ);
        return super.searchPageablePost(request);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Shift.DELETE);
        return super.delete(id);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        SecurityUtils.checkAuthority(Permissions.Shift.READ);
        return super.getById(id);
    }

    @Override
    public ResponseEntity<?> update(@Valid Shift object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Shift.UPDATE);
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> insert(@Valid Shift object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Shift.CREATE);
        return super.insert(object);
    }


}
