package com.example.erv.controller.role;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Role;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.role.RoleService;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class RoleControllerBasic extends GenericController<Role> {
    protected final RoleService roleService;

    public RoleControllerBasic(RoleService service) {
        super(service);
        this.roleService = service;
    }

    @Override
    public ResponseEntity<?> findAll() {
        SecurityUtils.checkAuthority(Permissions.Role.READ);
        return super.findAll();
    }

    @Override
    public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Role.READ);
        return super.searchPost(request);
    }

    @Override
    public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Role.READ);
        return super.searchPageablePost(request);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Role.DELETE);
        return super.delete(id);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        SecurityUtils.checkAuthority(Permissions.Role.READ);
        return super.getById(id);
    }

    @Override
    public ResponseEntity<?> update(@Valid Role object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Role.UPDATE);
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> insert(@Valid Role object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Role.CREATE);
        return super.insert(object);
    }


}
