package com.expensetracker.dto.expense;

import com.expensetracker.model.Expense;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExpenseRequest {
    
    @NotBlank(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency = "USD";
    
    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 200, message = "Description must be between 3 and 200 characters")
    private String description;
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;
    
    @NotNull(message = "Payment method is required")
    private Expense.PaymentMethod paymentMethod;
    
    @Size(max = 100, message = "Merchant name cannot exceed 100 characters")
    private String merchant;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    private List<String> tags = new ArrayList<>();
    
    private boolean recurring = false;
    
    private Expense.RecurringFrequency recurringFrequency;
    
    @Future(message = "Recurring end date must be in the future")
    private LocalDate recurringEndDate;
}
