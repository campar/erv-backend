package com.example.erv.config.security;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DeviceBasicFilter extends OncePerRequestFilter {

	private final String deviceUserName;
	private final String devicePassword;

	public DeviceBasicFilter(@Value("${device.user-name}") String deviceUserName, @Value("${device.password}") String devicePassword) {
		this.deviceUserName = deviceUserName;
		this.devicePassword = devicePassword;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Basic ")) {
			String base64Credentials = authHeader.substring("Basic ".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials));
			String[] values = credentials.split(":", 2);

			if (values.length == 2) {
				String username = values[0];
				String password = values[1];

				if (validateCredentials(username, password)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, List.of() // You can add roles or authorities if needed
					);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Credentials");
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean validateCredentials(String username, String password) {
		return username.equals(deviceUserName) && password.equals(devicePassword);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getRequestURI().startsWith("/api/external");
	}
}
