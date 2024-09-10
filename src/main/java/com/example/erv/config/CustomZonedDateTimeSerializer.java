package com.example.erv.config;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
	@Override
	public void serialize(ZonedDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
		jsonGenerator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}
}
