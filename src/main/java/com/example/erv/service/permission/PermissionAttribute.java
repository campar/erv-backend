
package com.example.erv.service.permission;

import com.example.erv.service.EntityAttribute;

public enum PermissionAttribute implements EntityAttribute {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description");

    private final String name;

    PermissionAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
