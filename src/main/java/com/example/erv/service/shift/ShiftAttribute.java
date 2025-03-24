package com.example.erv.service.shift;

import com.example.erv.service.EntityAttribute;

public enum ShiftAttribute implements EntityAttribute {
    ID("id"),
    START_FROM("startFrom"),
    START_TO("startTo"),
    END_FROM("endFrom"),
    END_TO("endTo"),
    DURATION_HOURS("durationHours"),
    DURATION_MINUTES("durationMinutes"),
    SOFT_DELETED("softDeleted");

    private final String name;

    ShiftAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
