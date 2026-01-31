package com.expensetracker.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "categories")
@CompoundIndex(name = "user_name_idx", def = "{'userId': 1, 'name': 1}", unique = true)
public class Category {
    
    @Id
    private String id;
    
    private String userId;
    
    private String name;
    
    private String description;
    
    private String icon;
    
    private String color;
    
    private CategoryType type = CategoryType.EXPENSE;
    
    private boolean defaultCategory = false;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum CategoryType {
        INCOME,
        EXPENSE
    }
}
