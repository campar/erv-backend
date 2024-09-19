package com.example.erv.config;

import java.time.Instant;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MapperConfig {
    private MapperConfig() {
    }

    public static ObjectMapper getObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());
        module.addDeserializer(Instant.class, new CustomInstantDeserializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }
}
