package com.expensetracker.dto.budget;

import com.expensetracker.model.Budget;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetRequest {
    
    private String categoryId; // Optional - null means overall budget
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency = "USD";
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @NotNull(message = "Budget period is required")
    private Budget.BudgetPeriod period;
    
    private boolean alertEnabled = true;
    
    @Min(value = 1, message = "Alert threshold must be at least 1%")
    @Max(value = 100, message = "Alert threshold cannot exceed 100%")
    private Integer alertThreshold = 80;
}
