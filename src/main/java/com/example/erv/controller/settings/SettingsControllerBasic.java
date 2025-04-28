package com.example.erv.controller.settings;

import com.example.erv.controller.GenericController;
import com.example.erv.model.Settings;
import com.example.erv.service.settings.SettingsService;

public class SettingsControllerBasic extends GenericController<Settings> {
    protected final SettingsService settingsService;

    public SettingsControllerBasic(SettingsService service) {
        super(service);
        this.settingsService = service;
    }

}
