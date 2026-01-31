package com.expensetracker.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String phoneNumber;
    
    private String profilePictureUrl;
    
    private Set<Role> roles = new HashSet<>();
    
    private boolean enabled = true;
    
    private boolean accountNonExpired = true;
    
    private boolean accountNonLocked = true;
    
    private boolean credentialsNonExpired = true;
    
    private String currency = "USD";
    
    private LocalDateTime lastLoginAt;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum Role {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_PREMIUM
    }
}
