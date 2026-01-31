package com.expensetracker.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "expenses")
@CompoundIndexes({
    @CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'date': -1}"),
    @CompoundIndex(name = "user_category_idx", def = "{'userId': 1, 'categoryId': 1}")
})
public class Expense {
    
    @Id
    private String id;
    
    private String userId;
    
    private String categoryId;
    
    private BigDecimal amount;
    
    private String currency = "USD";
    
    private String description;
    
    private LocalDate date;
    
    private PaymentMethod paymentMethod;
    
    private String merchant;
    
    private String notes;
    
    private List<String> tags = new ArrayList<>();
    
    private List<String> receiptUrls = new ArrayList<>();
    
    private boolean recurring = false;
    
    private RecurringFrequency recurringFrequency;
    
    private LocalDate recurringEndDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        NET_BANKING,
        WALLET,
        OTHER
    }
    
    public enum RecurringFrequency {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }
}
