package com.expensetracker.controller;

import com.expensetracker.dto.analytics.AnalyticsResponse;
import com.expensetracker.dto.common.ApiResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Analytics", description = "Advanced analytics and reporting APIs")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping
    @Operation(summary = "Get comprehensive analytics", 
               description = "Get detailed analytics including summary, category breakdown, trends, and top expenses")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AnalyticsResponse response = analyticsService.getAnalytics(userPrincipal.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get category-specific analytics", 
               description = "Get detailed analytics for a specific category")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategoryAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String categoryId,
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> response = analyticsService.getCategoryAnalytics(
                userPrincipal.getId(), categoryId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/monthly-comparison")
    @Operation(summary = "Get monthly comparison", 
               description = "Compare spending across all months of a year")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyComparison(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Year (e.g., 2024)") 
            @RequestParam int year) {
        Map<String, Object> response = analyticsService.getMonthlyComparison(userPrincipal.getId(), year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/current-month")
    @Operation(summary = "Get current month analytics", 
               description = "Quick access to current month's analytics")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getCurrentMonthAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        
        AnalyticsResponse response = analyticsService.getAnalytics(userPrincipal.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/last-30-days")
    @Operation(summary = "Get last 30 days analytics", 
               description = "Analytics for the last 30 days")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getLast30DaysAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        AnalyticsResponse response = analyticsService.getAnalytics(userPrincipal.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
