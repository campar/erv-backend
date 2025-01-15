package com.example.erv.config.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBlacklist {
    private static final Map<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public static void addToBlacklist(String token, long expiryTime) {
        tokenBlacklist.put(token, expiryTime);
    }

    public static boolean isTokenBlacklisted(String token) {
        Long expiryTime = tokenBlacklist.get(token);
        if (expiryTime == null) return false;
        
        if (System.currentTimeMillis() > expiryTime) {
            tokenBlacklist.remove(token);
            return false;
        }
        
        return true;
    }

    public static void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
