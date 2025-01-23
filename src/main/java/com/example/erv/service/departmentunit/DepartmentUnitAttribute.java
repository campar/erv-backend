package com.example.erv.service.departmentunit;

import com.example.erv.service.EntityAttribute;

public enum DepartmentUnitAttribute implements EntityAttribute {
    ID("id"),
    NAME("name"),
    CODE("code"),
    DESCRIPTION("description"),
    PARENT("parent"),
    USER_LIST("userList"),
	SOFT_DELETED("softDeleted");

    private final String name;

    DepartmentUnitAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
