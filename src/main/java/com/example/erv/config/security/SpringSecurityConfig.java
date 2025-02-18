package com.example.erv.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.erv.service.user.UserService;
import com.example.erv.utils.AppUtil;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {
	@Value("${device.user-name}")
	private String deviceUserName;
	@Value("${device.password}")
	private String devicePassword;
	@Value("${cors.allowed-origins}")
	private String[] allowedOrigins;
	private final UserService userService;
	private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private static final String[] EXCLUDED_URLS = { "/auth/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/api/external/**"};
	
	public SpringSecurityConfig(UserService userService, JwtFilter jwtFilter, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.jwtFilter = jwtFilter;
		this.userService = userService;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.headers(headers -> 
						{
							headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
							headers.contentSecurityPolicy(csp -> csp
				                    .policyDirectives("default-src 'self'; " +
		                                      "script-src 'self' 'nonce-random123'; " +
		                                      "style-src 'self' 'nonce-random123'; " +
		                                      "img-src 'self' data:; " +
		                                      "font-src 'self'; " +
		                                      "connect-src 'self'; " +
		                                      "frame-ancestors 'self'; " +
		                                      "base-uri 'none';")
									);
						}
					)
			.authorizeHttpRequests(req -> 
				req.requestMatchers(EXCLUDED_URLS)
				.permitAll()
				.anyRequest()
				.authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.userDetailsService(userService)
			.exceptionHandling(handling -> handling.authenticationEntryPoint(customAuthenticationEntryPoint))
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
	
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http, DeviceBasicFilter deviceBasicFilter) throws Exception {
	    return http
	        .securityMatcher("/api/external/**")
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
	        .addFilterBefore(deviceBasicFilter, UsernamePasswordAuthenticationFilter.class)
	        .build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return AppUtil.PASSWORD_ENCODER;
	}
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
