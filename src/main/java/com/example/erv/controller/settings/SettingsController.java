package com.example.erv.controller.settings;

import com.example.erv.model.Settings;
import com.example.erv.service.settings.SettingsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Primary
@CrossOrigin
@RestController
@RequestMapping("/api/settings")
@SecurityRequirement(name = "bearerAuth")
public class SettingsController extends SettingsControllerBasic {

    public SettingsController(SettingsService service) {
        super(service);
    }

    @PostMapping(value = "/saveAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAll(@RequestBody @NotNull List<Settings> settings) {
        List<Settings> saved = new ArrayList<>();
        for (Settings setting : settings) {
            saved.add(settingsService.save(setting));
        }
        return ResponseEntity.ok(saved);
    }
}
