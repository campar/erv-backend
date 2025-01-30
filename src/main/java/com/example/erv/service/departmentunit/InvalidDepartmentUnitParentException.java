package com.example.erv.service.departmentunit;

import com.example.erv.exceptions.ErvException;

public class InvalidDepartmentUnitParentException extends ErvException {
    public InvalidDepartmentUnitParentException(Object object) {
        super(object);
    }
}
