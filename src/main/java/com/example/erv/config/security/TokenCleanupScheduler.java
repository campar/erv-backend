package com.example.erv.config.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupScheduler {
    @Scheduled(fixedRate = 30 * 60 * 1000) // Every 30 minutes
    public void cleanupTokens() {
        TokenBlacklist.cleanupExpiredTokens();
    }
}