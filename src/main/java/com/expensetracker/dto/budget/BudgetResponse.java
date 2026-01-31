package com.expensetracker.dto.budget;

import com.expensetracker.model.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    
    private String id;
    private String userId;
    private String categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Budget.BudgetPeriod period;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private Integer percentageUsed;
    private boolean alertEnabled;
    private Integer alertThreshold;
    private boolean alertSent;
    private BudgetStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum BudgetStatus {
        ON_TRACK,
        WARNING,
        EXCEEDED
    }
}
