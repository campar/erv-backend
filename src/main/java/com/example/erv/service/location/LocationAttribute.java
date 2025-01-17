package com.example.erv.service.location;

import com.example.erv.service.EntityAttribute;

public enum LocationAttribute implements EntityAttribute {
    ID("id"),
    NAME("name"),
    ADDRESS("address"),
    ACTIVE("active"),
    CITY("city"),
    PHONE("phone"),
    DESCRIPTION("description"),
    DEVICE_LIST("deviceList"),
    SOFT_DELETED("softDeleted");

    private final String name;

    LocationAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
