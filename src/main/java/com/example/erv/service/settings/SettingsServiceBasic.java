package com.example.erv.service.settings;

import java.util.List;

import com.example.erv.dto.projection.SettingsMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Settings;
import com.example.erv.model.User;
import com.example.erv.repository.settings.SettingsRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class SettingsServiceBasic extends GenericService<Settings> {
    protected final SettingsRepository repository;

    public SettingsServiceBasic(SettingsRepository repository) {
        super(repository);
        this.repository = repository;
    }
    
    @Override
    public List<SettingsMinimal> findAllMinimal(){
		return repository.findAllProjected(SettingsMinimal.class);
	}

    @Override
    public Settings insert(Settings object) throws ErvException {
        if (object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
        return repository.save(object);
    }

    @Override
    public Settings update(Settings object) throws ErvException {
        if (object.getId() == null || repository.findById(object.getId()).isEmpty())
            throw new EntityNotFoundException(String.valueOf(object.getId()));
        return repository.save(object);
    }

    @Override
    public Class<Settings> getEntityClass() {
        return Settings.class;
    }

    public List<Settings> findByKey(String key) {
        return repository.findByKey(key);
    }

    public List<Settings> findByValue(String value) {
        return repository.findByValue(value);
    }

    public List<Settings> findByUser(User user) {
        return repository.findByUser(user);
    }
}
