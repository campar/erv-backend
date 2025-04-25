package com.example.erv.service.settings;

import com.example.erv.service.EntityAttribute;

public enum SettingsAttribute implements EntityAttribute {
    ID("id"),
    KEY("key"),
    VALUE("value"),
    USER("user");
    private final String name;

    SettingsAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
