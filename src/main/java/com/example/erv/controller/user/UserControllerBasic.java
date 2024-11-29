package com.example.erv.controller.user;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.User;
import com.example.erv.service.permission.Permissions;
import com.example.erv.service.user.UserService;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class UserControllerBasic extends GenericController<User> {
    protected final UserService userService;

    public UserControllerBasic(UserService service) {
        super(service);
        this.userService = service;
    }

    @Override
    public ResponseEntity<?> findAll() {
        SecurityUtils.checkAuthority(Permissions.User.READ);
        return super.findAll();
    }

    @Override
    public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.User.READ);
        return super.searchPost(request);
    }

    @Override
    public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.User.READ);
        return super.searchPageablePost(request);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.User.DELETE);
        return super.delete(id);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        SecurityUtils.checkAuthority(Permissions.User.READ);
        return super.getById(id);
    }

    @Override
    public ResponseEntity<?> update(@Valid User object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.User.UPDATE);
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> insert(@Valid User object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.User.CREATE);
        return super.insert(object);
    }


}
