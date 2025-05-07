package com.example.erv.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class BruteForceProtectionService {
	@Value("${brute.force.protection.feature.on}")
	private boolean bruteForceProtectionFeatureOn = true;
	@Value("${login.attempt.count}")
    private int maxAttempts = 5;
	@Value("${account.lock.time}")
    private long lockTimeLength = TimeUnit.MINUTES.toMillis(15);

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockCache = new ConcurrentHashMap<>();
    
    private String createKey(String username, String ipAddress) {
        return username + ":" + ipAddress;
    }

    public void loginSucceeded(String username, String ipAddress) {
        if (bruteForceProtectionFeatureOn) {
            String key = createKey(username, ipAddress);
            attemptsCache.remove(key); // Clear failed attempts on successful login
            lockCache.remove(key); // Unlock user on successful login
        }
    }

    public void loginFailed(String username, String ipAddress) {
        if (bruteForceProtectionFeatureOn) {
            String key = createKey(username, ipAddress);
            int attempts = attemptsCache.getOrDefault(key, 0);
            attempts++;
            attemptsCache.put(key, attempts);
            if (attempts >= maxAttempts) {
                lockCache.put(key, System.currentTimeMillis()); // Lock user if max attempts exceeded
            }
        }
    }

    public boolean isBlocked(String username, String ipAddress) {
        if (bruteForceProtectionFeatureOn) {
            String key = createKey(username, ipAddress);
            if (!lockCache.containsKey(key)) {
                return false;
            }

            long lockTime = lockCache.get(key);
            if (System.currentTimeMillis() - lockTime > lockTimeLength) {
                lockCache.remove(key); // Remove lock if lock time has expired
                return false;
            }

            return true; // User is still locked
        }
        return false;
    }

    public long getRemainingBlockTime(String username, String ipAddress) {
        if (bruteForceProtectionFeatureOn) {
            String key = createKey(username, ipAddress);
            Long lockTime = lockCache.get(key);
            if (lockTime == null) {
                return 0;
            }
            long elapsedTime = System.currentTimeMillis() - lockTime;
            return Math.max(0, lockTimeLength - elapsedTime) / 1000;
        }
        return 0L;
    }
}