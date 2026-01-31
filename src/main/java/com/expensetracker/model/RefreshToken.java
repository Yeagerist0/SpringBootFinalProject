package com.expensetracker.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "refresh_tokens")
public class RefreshToken {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    private String userId;
    
    @Indexed(expireAfterSeconds = 604800) // 7 days
    private LocalDateTime expiryDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
}
