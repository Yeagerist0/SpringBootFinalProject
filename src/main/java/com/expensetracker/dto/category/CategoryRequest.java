package com.expensetracker.dto.category;

import com.expensetracker.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;
    
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;
    
    @Size(max = 50, message = "Icon name cannot exceed 50 characters")
    private String icon;
    
    @Size(max = 7, message = "Color must be a valid hex color code")
    private String color;
    
    private Category.CategoryType type = Category.CategoryType.EXPENSE;
}
