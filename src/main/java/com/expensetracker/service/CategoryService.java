package com.expensetracker.service;

import com.expensetracker.dto.category.CategoryRequest;
import com.expensetracker.dto.category.CategoryResponse;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Category;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional
    public void createDefaultCategories(String userId) {
        List<CategoryData> defaultCategories = Arrays.asList(
                new CategoryData("Food & Dining", "Restaurant, groceries, etc.", "🍔", "#FF6B6B", Category.CategoryType.EXPENSE),
                new CategoryData("Transportation", "Fuel, taxi, public transport", "🚗", "#4ECDC4", Category.CategoryType.EXPENSE),
                new CategoryData("Shopping", "Clothes, electronics, etc.", "🛒", "#45B7D1", Category.CategoryType.EXPENSE),
                new CategoryData("Entertainment", "Movies, games, hobbies", "🎮", "#96CEB4", Category.CategoryType.EXPENSE),
                new CategoryData("Bills & Utilities", "Electricity, water, internet", "💡", "#FFEAA7", Category.CategoryType.EXPENSE),
                new CategoryData("Healthcare", "Medicine, doctor visits", "🏥", "#DFE6E9", Category.CategoryType.EXPENSE),
                new CategoryData("Education", "Books, courses, tuition", "📚", "#A29BFE", Category.CategoryType.EXPENSE),
                new CategoryData("Travel", "Vacation, trips", "✈️", "#FD79A8", Category.CategoryType.EXPENSE),
                new CategoryData("Salary", "Monthly salary, bonus", "💰", "#00B894", Category.CategoryType.INCOME),
                new CategoryData("Freelance", "Freelance projects", "💼", "#FDCB6E", Category.CategoryType.INCOME),
                new CategoryData("Other", "Miscellaneous", "📦", "#B2BEC3", Category.CategoryType.EXPENSE)
        );
        
        for (CategoryData data : defaultCategories) {
            Category category = new Category();
            category.setUserId(userId);
            category.setName(data.name);
            category.setDescription(data.description);
            category.setIcon(data.icon);
            category.setColor(data.color);
            category.setType(data.type);
            category.setDefaultCategory(true);
            
            categoryRepository.save(category);
        }
        
        log.info("Default categories created for user: {}", userId);
    }
    
    @Transactional
    @CacheEvict(value = "categories", key = "#userId")
    public CategoryResponse createCategory(String userId, CategoryRequest request) {
        // Check if category with same name already exists for user
        if (categoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = new Category();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setType(request.getType());
        category.setDefaultCategory(false);
        
        category = categoryRepository.save(category);
        log.info("Category created: {} for user: {}", category.getName(), userId);
        
        return mapToResponse(category);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#userId")
    public List<CategoryResponse> getAllCategories(String userId) {
        List<Category> categories = categoryRepository.findByUserIdOrDefaultCategoryTrue(userId);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String userId, String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Check if category belongs to user
        if (!category.getUserId().equals(userId) && !category.isDefaultCategory()) {
            throw new BadRequestException("You don't have access to this category");
        }
        
        return mapToResponse(category);
    }
    
    @Transactional
    @CacheEvict(value = "categories", key = "#userId")
    public CategoryResponse updateCategory(String userId, String categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Check if category belongs to user
        if (!category.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot update this category");
        }
        
        // Check if default category
        if (category.isDefaultCategory()) {
            throw new BadRequestException("Default categories cannot be updated");
        }
        
        // Check if name already exists (excluding current category)
        categoryRepository.findByUserIdAndName(userId, request.getName())
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(categoryId)) {
                        throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
                    }
                });
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setType(request.getType());
        
        category = categoryRepository.save(category);
        log.info("Category updated: {} for user: {}", category.getName(), userId);
        
        return mapToResponse(category);
    }
    
    @Transactional
    @CacheEvict(value = "categories", key = "#userId")
    public void deleteCategory(String userId, String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Check if category belongs to user
        if (!category.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot delete this category");
        }
        
        // Check if default category
        if (category.isDefaultCategory()) {
            throw new BadRequestException("Default categories cannot be deleted");
        }
        
        categoryRepository.delete(category);
        log.info("Category deleted: {} for user: {}", category.getName(), userId);
    }
    
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .userId(category.getUserId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .color(category.getColor())
                .type(category.getType())
                .isDefault(category.isDefaultCategory())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
    
    // Helper class for default categories
    private static class CategoryData {
        String name;
        String description;
        String icon;
        String color;
        Category.CategoryType type;
        
        CategoryData(String name, String description, String icon, String color, Category.CategoryType type) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.color = color;
            this.type = type;
        }
    }
}
