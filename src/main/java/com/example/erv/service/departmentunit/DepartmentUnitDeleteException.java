package com.example.erv.service.departmentunit;

import com.example.erv.exceptions.ErvException;

public class DepartmentUnitDeleteException extends ErvException {
    public static final String LOCALIZATION_KEY = "cannot_delete_departmentunit";

    public DepartmentUnitDeleteException(Long id) {
        super("Cannot delete object [" + id + "] because users with this department unit exist.");
    }
}
