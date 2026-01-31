package com.expensetracker.controller;

import com.expensetracker.dto.category.CategoryRequest;
import com.expensetracker.dto.category.CategoryResponse;
import com.expensetracker.dto.common.ApiResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.CategoryService;
import com.expensetracker.util.RateLimitUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final RateLimitUtil rateLimitUtil;
    
    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a custom expense/income category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CategoryRequest request) {
        rateLimitUtil.checkRateLimit("category-" + userPrincipal.getId());
        CategoryResponse response = categoryService.createCategory(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all categories for the user including default ones")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CategoryResponse> response = categoryService.getAllCategories(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by its ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String categoryId) {
        CategoryResponse response = categoryService.getCategoryById(userPrincipal.getId(), categoryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update an existing custom category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryRequest request) {
        rateLimitUtil.checkRateLimit("category-" + userPrincipal.getId());
        CategoryResponse response = categoryService.updateCategory(userPrincipal.getId(), categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }
    
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Delete a custom category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String categoryId) {
        categoryService.deleteCategory(userPrincipal.getId(), categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
