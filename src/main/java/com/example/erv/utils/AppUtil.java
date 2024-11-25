package com.example.erv.utils;

import java.time.Duration;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;

public class AppUtil {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private AppUtil() {
    }

    public static String encodePassword(String raw) {
        return PASSWORD_ENCODER.encode(raw);
    }

    public static String durationToString(Duration duration) {
        if (duration == null)
            return "-";
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static <K, V> Map<K, V> mergeMaps(Map<K, V> source, Map<K, V> destination) {
        destination.putAll(source);
        return destination;
    }
    
    public static String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // Handle cases where multiple IPs are listed (e.g., X-Forwarded-For: client1, proxy1, proxy2)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}
