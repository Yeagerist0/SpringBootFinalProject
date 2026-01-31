package com.expensetracker.service;

import com.expensetracker.dto.auth.*;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.UnauthorizedException;
import com.expensetracker.model.RefreshToken;
import com.expensetracker.model.User;
import com.expensetracker.repository.RefreshTokenRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtTokenProvider;
import com.expensetracker.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EmailUtil emailUtil;
    private final CategoryService categoryService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCurrency(request.getCurrency());
        
        // Set default role
        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.ROLE_USER);
        user.setRoles(roles);
        
        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
        
        // Create default categories for the user
        categoryService.createDefaultCategories(user.getId());
        
        // Send welcome email
        emailUtil.sendWelcomeEmail(user.getEmail(), user.getFirstName());
        
        // Generate tokens
        String accessToken = tokenProvider.generateToken(user.getId());
        String refreshToken = createRefreshToken(user.getId());
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = tokenProvider.generateToken(authentication);
        
        // Get user
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate refresh token
        String refreshToken = createRefreshToken(user.getId());
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        
        // Check if token is expired
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired. Please login again");
        }
        
        // Get user
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        // Generate new access token
        String accessToken = tokenProvider.generateToken(user.getId());
        
        log.info("Access token refreshed for user: {}", user.getUsername());
        
        return buildAuthResponse(user, accessToken, requestRefreshToken);
    }
    
    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("User logged out successfully: {}", userId);
    }
    
    private String createRefreshToken(String userId) {
        // Delete existing refresh tokens for user
        refreshTokenRepository.deleteByUserId(userId);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
    
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .currency(user.getCurrency())
                .lastLoginAt(user.getLastLoginAt())
                .build();
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getJwtExpiration())
                .user(userInfo)
                .build();
    }
}
