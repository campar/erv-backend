package com.example.erv.service.card;

import com.example.erv.service.EntityAttribute;

public enum CardAttribute implements EntityAttribute {
    ID("id"),
    PIN("pin"),
    RFID("rfid"),
    ACTIVE("active"),
    DESCRIPTION("description"),
    CREATION_DATE("creationDate"),
    EXPIRATION_DATE("expirationDate"),
    CARD_TYPE("cardType"),
    USER("user");

    private final String name;

    CardAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
