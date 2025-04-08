package com.example.erv.service.timetable;

import com.example.erv.service.EntityAttribute;

public enum TimetableAttribute implements EntityAttribute {
    ID("id"),
    ACTION_TYPE("actionType"),
    IMAGE("image"),
    DATE_TIME("dateTime"),
    USER("user"),    // Represents a ManyToOne relationship with the User entity
    SHIFT("shift"),  // Represents a ManyToOne relationship with the Shift entity
    DEVICE("device"); // Represents a ManyToOne relationship with the Device entity

    private final String name;

    TimetableAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
