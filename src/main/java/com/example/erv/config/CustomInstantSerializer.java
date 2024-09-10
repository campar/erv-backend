package com.example.erv.config;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomInstantSerializer extends JsonSerializer<Instant> {
	@Override
	public void serialize(Instant value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
		ZonedDateTime zonedDateTime = value.atZone(ZoneId.of(AppConfig.getTimezoneId()));
		jsonGenerator.writeString(zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}
}
