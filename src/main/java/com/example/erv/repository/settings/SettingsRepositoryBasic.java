package com.example.erv.repository.settings;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Settings;
import com.example.erv.model.User;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface SettingsRepositoryBasic extends GenericRepository<Settings> {

    List<Settings> findByUser(User user);

    List<Settings> findByKey(String key);

    List<Settings> findByValue(String value);
}
