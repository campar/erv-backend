package com.example.erv.exceptions;

import java.io.Serial;

public class DeviceContainsLocationException extends ErvException {
	@Serial
    private static final long serialVersionUID = 1L;
	public static final String LOCALIZATION_KEY = "location_is_connected_to_device";

	public DeviceContainsLocationException(String message) {
        super(message);
    }

}
