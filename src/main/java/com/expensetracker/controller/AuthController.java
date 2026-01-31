package com.expensetracker.controller;

import com.expensetracker.dto.auth.*;
import com.expensetracker.dto.common.ApiResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.AuthService;
import com.expensetracker.util.RateLimitUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization APIs")
public class AuthController {
    
    private final AuthService authService;
    private final RateLimitUtil rateLimitUtil;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        rateLimitUtil.checkRateLimit("register-" + request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        rateLimitUtil.checkRateLimit("login-" + request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Get a new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        authService.logout(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}
