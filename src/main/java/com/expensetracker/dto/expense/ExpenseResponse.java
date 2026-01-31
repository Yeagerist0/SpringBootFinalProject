package com.expensetracker.dto.expense;

import com.expensetracker.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    
    private String id;
    private String userId;
    private String categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDate date;
    private Expense.PaymentMethod paymentMethod;
    private String merchant;
    private String notes;
    private List<String> tags;
    private List<String> receiptUrls;
    private boolean recurring;
    private Expense.RecurringFrequency recurringFrequency;
    private LocalDate recurringEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
