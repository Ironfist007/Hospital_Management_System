package com.hospital.controller;

import com.hospital.dto.AuthRequest;
import com.hospital.dto.AuthResponse;
import com.hospital.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and generate JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        log.info("Login attempt for username: {}", authRequest.getUsername());
        
        try {
            // TEMPORARY: For testing - generate token for any username
            String token = jwtTokenProvider.generateToken(authRequest.getUsername());
            
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .username(authRequest.getUsername())
                    .message("Login successful")
                    .build();
            
            log.info("Login successful for username: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
                    .build();
            
            log.info("User logged in successfully: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            log.error("Authentication failed for username: {}", authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message("Authentication failed: Invalid credentials")
                            .build());
        }
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<Boolean> validateToken(
            @RequestHeader(value = "Authorization") String bearerToken) {
        log.info("Validating token");
        
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(false);
        }
        
        String token = bearerToken.substring(7);
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        return ResponseEntity.ok(isValid);
    }
}
