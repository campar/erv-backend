package com.example.erv.service.settings;

import com.example.erv.dto.settings.UserLanguageDTO;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Settings;
import com.example.erv.model.User;
import com.example.erv.repository.settings.SettingsRepository;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService extends SettingsServiceBasic {
    @Autowired
    private SecurityUtils securityUtils;

    public SettingsService(SettingsRepository repository) {
        super(repository);
    }

    public Optional<Settings> findByKeyForCurrentUser(String key) {
        User user = securityUtils.getCurrentUser();
        return repository.findByUserAndKey(user, key);
    }

    @Override
    public Settings save(Settings object) {
        Optional<Settings> optionalSettings = findByKeyForCurrentUser(object.getKey());
        if (optionalSettings.isPresent()) {
            Settings settings = optionalSettings.get();
            settings.setValue(object.getValue());
            return repository.save(settings);
        }
        object.setUser(securityUtils.getCurrentUser());
        return repository.save(object);
    }

    @Override
    public Settings insert(Settings object) throws ErvException {
        return save(object);
    }

    @Override
    public Settings update(Settings object) throws ErvException {
        Optional<Settings> optionalSettings = findByKeyForCurrentUser(object.getKey());
        if (optionalSettings.isPresent()) {
            Settings settings = optionalSettings.get();
            settings.setValue(object.getValue());
            return repository.save(settings);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public List<Settings> findAll() {
        User user = securityUtils.getCurrentUser();
        SpecificationBasic<Settings> spec = SpecificationBasic.where(SettingsAttribute.USER, user);
        return super.findAll(spec.getSpecification());
    }

    public Settings updateLanguageSettings(UserLanguageDTO request) {
        Optional<Settings> optional = findByKeyForCurrentUser("language");
        if (optional.isEmpty()) {
            User user = securityUtils.getCurrentUser();
            Settings settings = new Settings();
            settings.setUser(user);
            settings.setKey("language");
            settings.setValue(request.getLanguageCode());
            return save(settings);
        }
        Settings settings = optional.get();
        settings.setValue(request.getLanguageCode());
        return save(settings);
    }
}
