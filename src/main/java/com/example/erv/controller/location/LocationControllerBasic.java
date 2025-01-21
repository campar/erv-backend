package com.example.erv.controller.location;

import com.example.erv.controller.GenericController;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Location;
import com.example.erv.service.location.LocationService;
import com.example.erv.service.permission.Permissions;
import com.example.erv.specification.SearchDTO;
import com.example.erv.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public class LocationControllerBasic extends GenericController<Location> {
    protected final LocationService locationService;

    public LocationControllerBasic(LocationService service) {
        super(service);
        this.locationService = service;
    }

    @Override
    public ResponseEntity<?> findAll() {
        SecurityUtils.checkAuthority(Permissions.Location.READ);
        return super.findAll();
    }

    @Override
    public ResponseEntity<?> searchPost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Location.READ);
        return super.searchPost(request);
    }

    @Override
    public ResponseEntity<?> searchPageablePost(SearchDTO request) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Location.READ);
        return super.searchPageablePost(request);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Location.DELETE);
        return super.delete(id);
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        SecurityUtils.checkAuthority(Permissions.Location.READ);
        return super.getById(id);
    }

    @Override
    public ResponseEntity<?> update(@Valid Location object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Location.UPDATE);
        return super.update(object);
    }

    @Override
    public ResponseEntity<?> insert(@Valid Location object) throws ErvException {
        SecurityUtils.checkAuthority(Permissions.Location.CREATE);
        return super.insert(object);
    }


}
