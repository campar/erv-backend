package com.example.erv.controller.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erv.config.security.JwtUtil;
import com.example.erv.config.security.TokenBlacklist;
import com.example.erv.dto.ErrorResponse;
import com.example.erv.dto.GenericResponse;
import com.example.erv.dto.LoginRequest;
import com.example.erv.dto.PasswordChangeDTO;
import com.example.erv.dto.UserDataDTO;
import com.example.erv.exceptions.BlockedUserException;
import com.example.erv.exceptions.DeletedUserException;
import com.example.erv.exceptions.LocalizationKey;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.User;
import com.example.erv.service.BruteForceProtectionService;
import com.example.erv.service.user.UserService;
import com.example.erv.utils.AppUtil;
import com.example.erv.utils.SecurityUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService service;
    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final BruteForceProtectionService bruteForceProtectionService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService service, UserService userService, SecurityUtils securityUtils, BruteForceProtectionService bruteForceProtectionService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.service = service;
        this.userService = userService;
        this.securityUtils = securityUtils;
        this.bruteForceProtectionService = bruteForceProtectionService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken == null || jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(401).body(GenericResponse.getErrorResponse("401", "Token expired!"));
        }
        
        if (TokenBlacklist.isTokenBlacklisted(refreshToken)) {
            return ResponseEntity.status(401).body(GenericResponse.getErrorResponse("401", "Token is blacklisted!"));
        }
        
        String refreshedToken = jwtUtil.refreshToken(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("jwt", refreshedToken).httpOnly(true).secure(request.isSecure()).path("/").maxAge(jwtUtil.getJwtExpiration() / 1000).build();

        response.addHeader("Set-Cookie", cookie.toString());
        User user = service.findByUsername(jwtUtil.extractUsername(refreshedToken));
        UserDataDTO userData = new UserDataDTO(user.getUsername(), user.getRole().getPermissionList().stream().map(GrantedAuthority::getAuthority).toList());
        return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(userData));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest authRequest, HttpServletRequest request, HttpServletResponse response) throws BlockedUserException, DeletedUserException {
        String username = authRequest.getUsername();
        String ipAddress = AppUtil.getClientIpAddress(request);
        if(userService.findByUsername(username).getSoftDeleted()) throw new DeletedUserException("User was deleted.");
        
        if (bruteForceProtectionService.isBlocked(username, ipAddress)) {
        	throw new BlockedUserException(username, bruteForceProtectionService.getRemainingBlockTime(username, ipAddress));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
            bruteForceProtectionService.loginSucceeded(username, ipAddress);
            UserDataDTO userData = createUserData(response, authentication, request.isSecure());
            return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(userData));
        } catch (AuthenticationException e) {
            bruteForceProtectionService.loginFailed(username, ipAddress);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    private UserDataDTO createUserData(HttpServletResponse response, Authentication authentication, boolean secure) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt).httpOnly(true).secure(secure).path("/").maxAge(jwtUtil.getJwtExpiration() / 1000).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(secure).path("/auth/").maxAge(jwtUtil.getRefreshTokenExpiration() / 1000).build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        return new UserDataDTO(userDetails.getUsername(), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(name = "jwt", required = false) String jwtCookie,
                                    @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie, HttpServletRequest request) {
        if (jwtCookie != null) {
        	TokenBlacklist.addToBlacklist(jwtCookie, jwtUtil.getExpirationTime(jwtCookie));
            ResponseCookie cookie = ResponseCookie.from("jwt", null).httpOnly(true).secure(request.isSecure()).path("/").maxAge(0).build();
            response.addHeader("Set-Cookie", cookie.toString());
        }
        if (refreshTokenCookie != null) {
        	TokenBlacklist.addToBlacklist(refreshTokenCookie, jwtUtil.getExpirationTime(refreshTokenCookie));
            ResponseCookie cookie = ResponseCookie.from("refreshToken", null).httpOnly(true).secure(request.isSecure()).path("/auth/").maxAge(0).build();
            response.addHeader("Set-Cookie", cookie.toString());
        }
        
        request.getSession().invalidate();

        return ResponseEntity.ok(GenericResponse.getSuccessfulResponse("User logged out successfully!"));
    }

    
    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> changePassword(@CookieValue(name = "jwt", required = false) String jwtCookie, @CookieValue(name = "refreshToken", required = false) String refreshToken, @RequestBody @Valid PasswordChangeDTO passwordChangeDTO, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (jwtCookie == null || refreshToken == null || jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(401).body(GenericResponse.getErrorResponse("401", "Invalid or expired token."));
        }
        
        if(securityUtils.getCurrentUser().getId().equals(passwordChangeDTO.getId())) {
        	try {
        		User user = userService.changePassword(passwordChangeDTO);
        		logout(response, jwtCookie, refreshToken, request);
        		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), passwordChangeDTO.getNewPassword()));
        		UserDataDTO userData = createUserData(response, authentication, request.isSecure());
        		return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(userData));
        	} catch (ErvException e) {
        		return ResponseEntity.status(400).body(GenericResponse.getErrorResponse("400", "Cannot save user."));
        	}
        }else {
        	throw new AccessDeniedException("Access denied! You do not have permission for this action.");
        }
    }

    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(@CookieValue(name = "jwt", required = false) String jwtCookie, @CookieValue(name = "refreshToken", required = false) String refreshToken,
                                             HttpServletResponse response, HttpServletRequest request) {
        if (refreshToken == null || jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(401).body(new ErrorResponse("401", "Invalid or expired token", LocalizationKey.EXPIRED_SESSION));
        }

        if ((jwtCookie != null && TokenBlacklist.isTokenBlacklisted(jwtCookie)) || TokenBlacklist.isTokenBlacklisted(refreshToken)) {
            return ResponseEntity.status(401).body(new ErrorResponse("401", "Token is blacklisted", LocalizationKey.EXPIRED_SESSION));
        }
        
        if (jwtCookie == null) {
            String refreshedToken = jwtUtil.refreshToken(refreshToken);
            ResponseCookie cookie = ResponseCookie.from("jwt", refreshedToken).httpOnly(true).secure(request.isSecure()).path("/").maxAge(jwtUtil.getJwtExpiration() / 1000).build();
            response.addHeader("Set-Cookie", cookie.toString());

            User user = service.findByUsername(jwtUtil.extractUsername(refreshedToken));
            UserDataDTO userData = new UserDataDTO(user.getUsername(), user.getRole().getPermissionList().stream().map(GrantedAuthority::getAuthority).toList());

            return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(userData));
        }

        User user = service.findByUsername(jwtUtil.extractUsername(jwtCookie));
        UserDataDTO userData = new UserDataDTO(user.getUsername(), user.getRole().getPermissionList().stream().map(GrantedAuthority::getAuthority).toList());

        return ResponseEntity.ok(GenericResponse.getSuccessfulResponse(userData));
    }
    
}