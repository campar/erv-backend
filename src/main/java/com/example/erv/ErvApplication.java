package com.example.erv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
@EnableCaching
@EnableScheduling
public class ErvApplication {
	public static void main(String[] args) {
		SpringApplication.run(ErvApplication.class, args);
	}
}
