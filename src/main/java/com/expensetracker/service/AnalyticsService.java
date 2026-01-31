package com.expensetracker.service;

import com.expensetracker.dto.analytics.AnalyticsResponse;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final MongoTemplate mongoTemplate;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "analytics", key = "#userId + '-' + #startDate + '-' + #endDate")
    public AnalyticsResponse getAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findExpensesByUserAndDateRange(userId, startDate, endDate);
        
        // Calculate summary
        AnalyticsResponse.Summary summary = calculateSummary(expenses);
        
        // Calculate category breakdown
        List<AnalyticsResponse.CategoryBreakdown> categoryBreakdown = calculateCategoryBreakdown(expenses, userId);
        
        // Calculate monthly trends
        List<AnalyticsResponse.MonthlyTrend> monthlyTrends = calculateMonthlyTrends(expenses);
        
        // Calculate payment method breakdown
        Map<String, BigDecimal> paymentMethodBreakdown = calculatePaymentMethodBreakdown(expenses);
        
        // Get top expenses
        List<AnalyticsResponse.TopExpense> topExpenses = getTopExpenses(expenses, 10);
        
        return AnalyticsResponse.builder()
                .summary(summary)
                .categoryBreakdown(categoryBreakdown)
                .monthlyTrends(monthlyTrends)
                .paymentMethodBreakdown(paymentMethodBreakdown)
                .topExpenses(topExpenses)
                .build();
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryAnalytics", key = "#userId + '-' + #categoryId + '-' + #startDate + '-' + #endDate")
    public Map<String, Object> getCategoryAnalytics(String userId, String categoryId, 
                                                     LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findExpensesByUserCategoryAndDateRange(
                userId, categoryId, startDate, endDate);
        
        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageAmount = expenses.isEmpty() 
                ? BigDecimal.ZERO
                : totalAmount.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalExpenses", totalAmount);
        analytics.put("transactionCount", expenses.size());
        analytics.put("averageExpense", averageAmount);
        analytics.put("highestExpense", expenses.stream()
                .map(Expense::getAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO));
        analytics.put("lowestExpense", expenses.stream()
                .map(Expense::getAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO));
        
        return analytics;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getMonthlyComparison(String userId, int year) {
        Map<String, Object> comparison = new HashMap<>();
        
        for (int month = 1; month <= 12; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            
            List<Expense> expenses = expenseRepository.findExpensesByUserAndDateRange(userId, startDate, endDate);
            BigDecimal monthlyTotal = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            comparison.put(startDate.getMonth().name(), monthlyTotal);
        }
        
        return comparison;
    }
    
    private AnalyticsResponse.Summary calculateSummary(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            return AnalyticsResponse.Summary.builder()
                    .totalExpenses(BigDecimal.ZERO)
                    .totalIncome(BigDecimal.ZERO)
                    .balance(BigDecimal.ZERO)
                    .transactionCount(0L)
                    .averageExpense(BigDecimal.ZERO)
                    .highestExpense(BigDecimal.ZERO)
                    .lowestExpense(BigDecimal.ZERO)
                    .build();
        }
        
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageExpense = totalExpenses.divide(
                BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);
        
        BigDecimal highestExpense = expenses.stream()
                .map(Expense::getAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal lowestExpense = expenses.stream()
                .map(Expense::getAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        return AnalyticsResponse.Summary.builder()
                .totalExpenses(totalExpenses)
                .totalIncome(BigDecimal.ZERO)
                .balance(BigDecimal.ZERO.subtract(totalExpenses))
                .transactionCount((long) expenses.size())
                .averageExpense(averageExpense)
                .highestExpense(highestExpense)
                .lowestExpense(lowestExpense)
                .build();
    }
    
    private List<AnalyticsResponse.CategoryBreakdown> calculateCategoryBreakdown(
            List<Expense> expenses, String userId) {
        
        Map<String, List<Expense>> expensesByCategory = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategoryId));
        
        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<AnalyticsResponse.CategoryBreakdown> breakdown = new ArrayList<>();
        
        for (Map.Entry<String, List<Expense>> entry : expensesByCategory.entrySet()) {
            String categoryId = entry.getKey();
            List<Expense> categoryExpenses = entry.getValue();
            
            BigDecimal categoryAmount = categoryExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            double percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0
                    ? categoryAmount.multiply(BigDecimal.valueOf(100))
                            .divide(totalAmount, 2, RoundingMode.HALF_UP)
                            .doubleValue()
                    : 0.0;
            
            String categoryName = getCategoryName(categoryId);
            
            breakdown.add(AnalyticsResponse.CategoryBreakdown.builder()
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .amount(categoryAmount)
                    .count((long) categoryExpenses.size())
                    .percentage(percentage)
                    .build());
        }
        
        // Sort by amount descending
        breakdown.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        
        return breakdown;
    }
    
    private List<AnalyticsResponse.MonthlyTrend> calculateMonthlyTrends(List<Expense> expenses) {
        Map<String, List<Expense>> expensesByMonth = expenses.stream()
                .collect(Collectors.groupingBy(expense -> 
                        expense.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))));
        
        List<AnalyticsResponse.MonthlyTrend> trends = new ArrayList<>();
        
        for (Map.Entry<String, List<Expense>> entry : expensesByMonth.entrySet()) {
            String month = entry.getKey();
            List<Expense> monthExpenses = entry.getValue();
            
            BigDecimal monthAmount = monthExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            trends.add(AnalyticsResponse.MonthlyTrend.builder()
                    .month(month)
                    .amount(monthAmount)
                    .count((long) monthExpenses.size())
                    .build());
        }
        
        // Sort by month
        trends.sort(Comparator.comparing(AnalyticsResponse.MonthlyTrend::getMonth));
        
        return trends;
    }
    
    private Map<String, BigDecimal> calculatePaymentMethodBreakdown(List<Expense> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getPaymentMethod().name(),
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));
    }
    
    private List<AnalyticsResponse.TopExpense> getTopExpenses(List<Expense> expenses, int limit) {
        return expenses.stream()
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .limit(limit)
                .map(expense -> AnalyticsResponse.TopExpense.builder()
                        .id(expense.getId())
                        .description(expense.getDescription())
                        .amount(expense.getAmount())
                        .categoryName(getCategoryName(expense.getCategoryId()))
                        .date(expense.getDate().toString())
                        .build())
                .collect(Collectors.toList());
    }
    
    private String getCategoryName(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Unknown");
    }
}
