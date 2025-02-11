package com.example.erv.service.device;

import com.example.erv.service.EntityAttribute;

public enum DeviceAttribute implements EntityAttribute {
    ID("id"),
    CODE("code"),
    HARDWARE_IDENTIFIER("hardwareIdentifier"),
    ACTIVE("active"),
    SOFT_DELETED("softDeleted"),
    DESCRIPTION("description"),
    LOCATION("location"),
    TIMETABLE_LIST("timetableList");  // Including timetableList field

    private final String name;

    DeviceAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
