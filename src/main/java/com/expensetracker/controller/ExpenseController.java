package com.expensetracker.controller;

import com.expensetracker.dto.common.ApiResponse;
import com.expensetracker.dto.common.PagedResponse;
import com.expensetracker.dto.expense.ExpenseRequest;
import com.expensetracker.dto.expense.ExpenseResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.util.RateLimitUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Expenses", description = "Expense management APIs with pagination, filtering, and file upload")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    private final RateLimitUtil rateLimitUtil;
    
    @PostMapping
    @Operation(summary = "Create a new expense", description = "Add a new expense entry")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ExpenseRequest request) {
        rateLimitUtil.checkRateLimit("expense-" + userPrincipal.getId());
        ExpenseResponse response = expenseService.createExpense(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense created successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all expenses with pagination", 
               description = "Retrieve all expenses with pagination and sorting")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getAllExpenses(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        PagedResponse<ExpenseResponse> response = expenseService.getAllExpenses(
                userPrincipal.getId(), page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{expenseId}")
    @Operation(summary = "Get expense by ID", description = "Retrieve a specific expense by its ID")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String expenseId) {
        ExpenseResponse response = expenseService.getExpenseById(userPrincipal.getId(), expenseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/filter/date-range")
    @Operation(summary = "Filter expenses by date range", 
               description = "Get expenses within a specific date range with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getExpensesByDateRange(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<ExpenseResponse> response = expenseService.getExpensesByDateRange(
                userPrincipal.getId(), startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/filter/category/{categoryId}")
    @Operation(summary = "Filter expenses by category", 
               description = "Get expenses for a specific category with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getExpensesByCategory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<ExpenseResponse> response = expenseService.getExpensesByCategory(
                userPrincipal.getId(), categoryId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{expenseId}")
    @Operation(summary = "Update expense", description = "Update an existing expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String expenseId,
            @Valid @RequestBody ExpenseRequest request) {
        rateLimitUtil.checkRateLimit("expense-" + userPrincipal.getId());
        ExpenseResponse response = expenseService.updateExpense(userPrincipal.getId(), expenseId, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }
    
    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete expense", description = "Delete an expense entry")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String expenseId) {
        expenseService.deleteExpense(userPrincipal.getId(), expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }
    
    @PostMapping("/{expenseId}/receipt")
    @Operation(summary = "Upload receipt", description = "Upload receipt image/PDF for an expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> uploadReceipt(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String expenseId,
            @Parameter(description = "Receipt file (image or PDF)") 
            @RequestParam("file") MultipartFile file) {
        rateLimitUtil.checkRateLimit("upload-" + userPrincipal.getId());
        ExpenseResponse response = expenseService.uploadReceipt(userPrincipal.getId(), expenseId, file);
        return ResponseEntity.ok(ApiResponse.success("Receipt uploaded successfully", response));
    }
}
