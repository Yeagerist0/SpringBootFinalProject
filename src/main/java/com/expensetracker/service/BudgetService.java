package com.expensetracker.service;

import com.expensetracker.dto.budget.BudgetRequest;
import com.expensetracker.dto.budget.BudgetResponse;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Budget;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final EmailUtil emailUtil;
    
    @Transactional
    @CacheEvict(value = "budgets", key = "#userId")
    public BudgetResponse createBudget(String userId, BudgetRequest request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        // Validate category if provided
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }
        
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(request.getCategoryId());
        budget.setAmount(request.getAmount());
        budget.setCurrency(request.getCurrency());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());
        budget.setAlertEnabled(request.isAlertEnabled());
        budget.setAlertThreshold(request.getAlertThreshold());
        
        // Calculate spent amount
        BigDecimal spentAmount = calculateSpentAmount(userId, request.getCategoryId(), 
                request.getStartDate(), request.getEndDate());
        budget.setSpentAmount(spentAmount);
        budget.setRemainingAmount(request.getAmount().subtract(spentAmount));
        
        budget = budgetRepository.save(budget);
        log.info("Budget created: {} for user: {}", budget.getId(), userId);
        
        // Check if alert should be sent
        checkAndSendAlert(budget);
        
        return mapToResponse(budget);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "budgets", key = "#userId")
    public List<BudgetResponse> getAllBudgets(String userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BudgetResponse> getActiveBudgets(String userId) {
        LocalDate now = LocalDate.now();
        List<Budget> budgets = budgetRepository.findActiveBudgetsForUser(userId, now);
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(String userId, String budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        // Check if budget belongs to user
        if (!budget.getUserId().equals(userId)) {
            throw new BadRequestException("You don't have access to this budget");
        }
        
        return mapToResponse(budget);
    }
    
    @Transactional
    @CacheEvict(value = "budgets", key = "#userId")
    public BudgetResponse updateBudget(String userId, String budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        // Check if budget belongs to user
        if (!budget.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot update this budget");
        }
        
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        // Validate category if provided
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }
        
        budget.setCategoryId(request.getCategoryId());
        budget.setAmount(request.getAmount());
        budget.setCurrency(request.getCurrency());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());
        budget.setAlertEnabled(request.isAlertEnabled());
        budget.setAlertThreshold(request.getAlertThreshold());
        
        // Recalculate spent amount
        BigDecimal spentAmount = calculateSpentAmount(userId, request.getCategoryId(), 
                request.getStartDate(), request.getEndDate());
        budget.setSpentAmount(spentAmount);
        budget.setRemainingAmount(request.getAmount().subtract(spentAmount));
        budget.setAlertSent(false); // Reset alert
        
        budget = budgetRepository.save(budget);
        log.info("Budget updated: {} for user: {}", budget.getId(), userId);
        
        // Check if alert should be sent
        checkAndSendAlert(budget);
        
        return mapToResponse(budget);
    }
    
    @Transactional
    @CacheEvict(value = "budgets", key = "#userId")
    public void deleteBudget(String userId, String budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        // Check if budget belongs to user
        if (!budget.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot delete this budget");
        }
        
        budgetRepository.delete(budget);
        log.info("Budget deleted: {} for user: {}", budgetId, userId);
    }
    
    @Transactional
    public void updateBudgetSpentAmount(String userId, String categoryId, LocalDate date) {
        List<Budget> budgets = budgetRepository.findActiveBudgetsForUser(userId, date);
        
        for (Budget budget : budgets) {
            // Update only if budget is for all categories or matches the category
            if (budget.getCategoryId() == null || budget.getCategoryId().equals(categoryId)) {
                BigDecimal spentAmount = calculateSpentAmount(userId, budget.getCategoryId(), 
                        budget.getStartDate(), budget.getEndDate());
                
                budget.setSpentAmount(spentAmount);
                budget.setRemainingAmount(budget.getAmount().subtract(spentAmount));
                
                budgetRepository.save(budget);
                
                // Check if alert should be sent
                checkAndSendAlert(budget);
            }
        }
    }
    
    private BigDecimal calculateSpentAmount(String userId, String categoryId, 
                                            LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses;
        
        if (categoryId != null) {
            expenses = expenseRepository.findExpensesByUserCategoryAndDateRange(
                    userId, categoryId, startDate, endDate);
        } else {
            expenses = expenseRepository.findExpensesByUserAndDateRange(
                    userId, startDate, endDate);
        }
        
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void checkAndSendAlert(Budget budget) {
        if (!budget.isAlertEnabled() || budget.isAlertSent()) {
            return;
        }
        
        BigDecimal percentage = budget.getSpentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
        
        if (percentage.compareTo(BigDecimal.valueOf(budget.getAlertThreshold())) >= 0) {
            // Send alert email
            User user = userRepository.findById(budget.getUserId()).orElse(null);
            if (user != null) {
                String categoryName = budget.getCategoryId() != null 
                        ? getCategoryName(budget.getCategoryId())
                        : "Overall";
                
                emailUtil.sendBudgetAlertEmail(
                        user.getEmail(),
                        categoryName,
                        percentage.doubleValue(),
                        budget.getAmount().doubleValue()
                );
                
                budget.setAlertSent(true);
                budgetRepository.save(budget);
                
                log.info("Budget alert sent for budget: {}", budget.getId());
            }
        }
    }
    
    private String getCategoryName(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Unknown");
    }
    
    private BudgetResponse mapToResponse(Budget budget) {
        String categoryName = budget.getCategoryId() != null 
                ? getCategoryName(budget.getCategoryId())
                : null;
        
        int percentageUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
                ? budget.getSpentAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(budget.getAmount(), 0, RoundingMode.HALF_UP)
                        .intValue()
                : 0;
        
        BudgetResponse.BudgetStatus status;
        if (percentageUsed >= 100) {
            status = BudgetResponse.BudgetStatus.EXCEEDED;
        } else if (percentageUsed >= budget.getAlertThreshold()) {
            status = BudgetResponse.BudgetStatus.WARNING;
        } else {
            status = BudgetResponse.BudgetStatus.ON_TRACK;
        }
        
        return BudgetResponse.builder()
                .id(budget.getId())
                .userId(budget.getUserId())
                .categoryId(budget.getCategoryId())
                .categoryName(categoryName)
                .amount(budget.getAmount())
                .currency(budget.getCurrency())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .period(budget.getPeriod())
                .spentAmount(budget.getSpentAmount())
                .remainingAmount(budget.getRemainingAmount())
                .percentageUsed(percentageUsed)
                .alertEnabled(budget.isAlertEnabled())
                .alertThreshold(budget.getAlertThreshold())
                .alertSent(budget.isAlertSent())
                .status(status)
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}
