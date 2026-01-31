package com.expensetracker.controller;

import com.expensetracker.dto.budget.BudgetRequest;
import com.expensetracker.dto.budget.BudgetResponse;
import com.expensetracker.dto.common.ApiResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.BudgetService;
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
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Budgets", description = "Budget management APIs with alerts and tracking")
public class BudgetController {
    
    private final BudgetService budgetService;
    private final RateLimitUtil rateLimitUtil;
    
    @PostMapping
    @Operation(summary = "Create a new budget", description = "Set up a budget for a category or overall spending")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody BudgetRequest request) {
        rateLimitUtil.checkRateLimit("budget-" + userPrincipal.getId());
        BudgetResponse response = budgetService.createBudget(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all budgets", description = "Retrieve all budgets for the user")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAllBudgets(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BudgetResponse> response = budgetService.getAllBudgets(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active budgets", description = "Retrieve currently active budgets")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getActiveBudgets(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BudgetResponse> response = budgetService.getActiveBudgets(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{budgetId}")
    @Operation(summary = "Get budget by ID", description = "Retrieve a specific budget by its ID")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String budgetId) {
        BudgetResponse response = budgetService.getBudgetById(userPrincipal.getId(), budgetId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{budgetId}")
    @Operation(summary = "Update budget", description = "Update an existing budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String budgetId,
            @Valid @RequestBody BudgetRequest request) {
        rateLimitUtil.checkRateLimit("budget-" + userPrincipal.getId());
        BudgetResponse response = budgetService.updateBudget(userPrincipal.getId(), budgetId, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }
    
    @DeleteMapping("/{budgetId}")
    @Operation(summary = "Delete budget", description = "Delete a budget")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String budgetId) {
        budgetService.deleteBudget(userPrincipal.getId(), budgetId);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }
}
