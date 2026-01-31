package com.expensetracker.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    
    private String refreshToken;
    
    @Builder.Default
    private String tokenType = "Bearer";
    
    private Long expiresIn;
    
    private UserInfo user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Set<String> roles;
        private String currency;
        private LocalDateTime lastLoginAt;
    }
}
