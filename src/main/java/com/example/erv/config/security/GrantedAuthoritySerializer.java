package com.example.erv.config.security;

import java.io.IOException;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class GrantedAuthoritySerializer extends StdSerializer<GrantedAuthority> {
	private static final long serialVersionUID = 1L;

	public GrantedAuthoritySerializer() {
        super(GrantedAuthority.class);
    }

    @Override
    public void serialize(GrantedAuthority value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getAuthority());
    }
}