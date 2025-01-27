package com.example.erv.controller.departmentunit;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.example.erv.service.permission.Permissions;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class DepartmentUnitControllerBasic extends GenericController<DepartmentUnit> {
    protected final DepartmentUnitService departmentUnitService;

    public DepartmentUnitControllerBasic(DepartmentUnitService service) {
        super(service);
        this.departmentUnitService = service;
    }

    @Override
    public ResponseEntity<?> findAll() {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.READ);
        return super.findAll();
    }

    @Override
    public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.READ);
        return super.searchPost(request);
    }

    @Override
    public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.READ);
        return super.searchPageablePost(request);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.DELETE);
        return super.delete(id);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.READ);
        return super.getById(id);
    }

    @Override
    public ResponseEntity<?> update(@Valid DepartmentUnit object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.UPDATE);
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> insert(@Valid DepartmentUnit object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.DepartmentUnit.CREATE);
        return super.insert(object);
    }


}
