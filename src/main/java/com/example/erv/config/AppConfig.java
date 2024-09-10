package com.example.erv.config;

import java.time.Instant;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class AppConfig {
	private static String timezoneId;
	
	@Bean
	public com.fasterxml.jackson.databind.Module instantSerializer() {
		SimpleModule module = new SimpleModule();
	    module.addSerializer(Instant.class, new CustomInstantSerializer());
	    return module;
	}

	@Bean
	public com.fasterxml.jackson.databind.Module instantDeserializer() {
	    SimpleModule module = new SimpleModule();
	    module.addDeserializer(Instant.class, new CustomInstantDeserializer());
	    return module;
	}
	
	@Bean
	public com.fasterxml.jackson.databind.Module zonedDateTimeSerializer() {
		SimpleModule module = new SimpleModule();
	    module.addSerializer(ZonedDateTime.class, new CustomZonedDateTimeSerializer());
	    return module;
	}
	
	@Bean
	public com.fasterxml.jackson.databind.Module zonedDateTimeDeserializer() {
	    SimpleModule module = new SimpleModule();
	    module.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());
	    return module;
	}
	
	@Value("${app.configuration.timezone-id}")
	public void setTimezoneId(String timezoneId) {
		AppConfig.timezoneId = timezoneId;
	}
	
	public static String getTimezoneId() {
		return timezoneId;
	}
}
