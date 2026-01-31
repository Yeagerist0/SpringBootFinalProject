package com.expensetracker.dto.category;

import com.expensetracker.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    
    private String id;
    private String userId;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Category.CategoryType type;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
