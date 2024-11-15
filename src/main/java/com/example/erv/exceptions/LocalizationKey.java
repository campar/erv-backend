package com.example.erv.exceptions;

public enum LocalizationKey {
    EXPIRED_SESSION("expired_session"),
    ACCESS_DENIED("access_denied"),
    UNIQUE_VALUE_CONSTRAINT("unique_value_constraint"),
    BAD_CREDENTIALS("bad_credentials"),
    ALREADY_CHECKED_IN("already_checked_in"),
    ALREADY_CHECKED_OUT("already_checked_out"),
    ALREADY_STARTED_PAUSE("already_started_pause"),
    ALREADY_ENDED_PAUSE("already_ended_pause"),
    BUSINESS_STARTED_ALREADY("business_started_already"),
    BUSINESS_ENDED_ALREADY("business_ended_already"),
    NO_CHECK_IN("no_check_in"),
    NO_PAUSE_START("no_pause_start"),
    NO_BUSINESS_START("no_business_start"),
    INVALID_ACTION("invalid_action"),
    INVALID_CARD("invalid_card"),
    NO_DEVICE_IDENTIFIER("no_device_identifier"),
    NO_USER_SHIFT_ASSIGNED("no_user_shift_assigned"),
    INACTIVE_DEVICE("inactive_device"),
    TOO_MANY_LOGIN_ATTEMPTS("too_many_login_attempts"),
    CURRENT_PASSWORD_MISMATCH("current_password_mismatch");

    private final String key;

    LocalizationKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
