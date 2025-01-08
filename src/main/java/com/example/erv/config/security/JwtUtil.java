package com.example.erv.config.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.erv.model.User;
import com.example.erv.service.user.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

@Data
@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
    private long jwtExpiration;
	@Value("${jwt.refreshToken}")
	private long refreshTokenExpiration;
	@Autowired
	private UserService userService;
	
	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}
	
	public boolean isTokenExpired(String token){
		return extractAllClaims(token).getExpiration().before(new Date());
	}
	
	public String generateToken(String username){
		return createToken(username, jwtExpiration);
	}
	
	public String generateRefreshToken(String username){
		return createToken(username, refreshTokenExpiration);
	}
	
	public User getUserFromToken(String token) {
		return userService.findByUsername(extractUsername(token));
	}
	
	public String refreshToken(String token) {
        Claims claims = extractAllClaims(token);
        claims.setIssuedAt(new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

	public boolean validateToken(String token, UserDetails user) {
		return (user.getUsername().equals(extractUsername(token)) && !isTokenExpired(token));
	}
	
	
	private String createToken(String username, long expiration) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	public long getExpirationTime(String token) {
	    try {
	        return extractAllClaims(token).getExpiration().getTime();
	    } catch (Exception e) {
	        return 0;
	    }
	}
}
