package com.example.erv.service.role;

import com.example.erv.service.EntityAttribute;

public enum RoleAttribute implements EntityAttribute {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    PERMISSION_LIST("permissionList");

    private final String name;

    RoleAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
