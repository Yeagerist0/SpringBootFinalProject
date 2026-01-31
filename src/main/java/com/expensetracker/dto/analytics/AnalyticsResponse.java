package com.expensetracker.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    
    private Summary summary;
    private List<CategoryBreakdown> categoryBreakdown;
    private List<MonthlyTrend> monthlyTrends;
    private Map<String, BigDecimal> paymentMethodBreakdown;
    private List<TopExpense> topExpenses;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private BigDecimal totalExpenses;
        private BigDecimal totalIncome;
        private BigDecimal balance;
        private Long transactionCount;
        private BigDecimal averageExpense;
        private BigDecimal highestExpense;
        private BigDecimal lowestExpense;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private String categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTrend {
        private String month;
        private BigDecimal amount;
        private Long count;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopExpense {
        private String id;
        private String description;
        private BigDecimal amount;
        private String categoryName;
        private String date;
    }
}
