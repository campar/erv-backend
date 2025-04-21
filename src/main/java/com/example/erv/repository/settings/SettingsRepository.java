package com.example.erv.repository.settings;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.erv.model.Settings;
import com.example.erv.model.User;

@Repository
public interface SettingsRepository extends SettingsRepositoryBasic {
    Optional<Settings> findByUserAndKey(User user, String key);
}
