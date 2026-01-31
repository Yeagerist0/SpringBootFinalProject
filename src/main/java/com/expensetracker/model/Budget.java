package com.expensetracker.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "budgets")
@CompoundIndex(name = "user_period_idx", def = "{'userId': 1, 'startDate': -1}")
public class Budget {
    
    @Id
    private String id;
    
    private String userId;
    
    private String categoryId;
    
    private BigDecimal amount;
    
    private String currency = "USD";
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BudgetPeriod period = BudgetPeriod.MONTHLY;
    
    private BigDecimal spentAmount = BigDecimal.ZERO;
    
    private BigDecimal remainingAmount;
    
    private boolean alertEnabled = true;
    
    private Integer alertThreshold = 80; // Alert at 80% of budget
    
    private boolean alertSent = false;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum BudgetPeriod {
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY,
        CUSTOM
    }
}
