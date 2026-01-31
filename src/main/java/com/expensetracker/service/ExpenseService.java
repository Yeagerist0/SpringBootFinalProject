package com.expensetracker.service;

import com.expensetracker.dto.common.PagedResponse;
import com.expensetracker.dto.expense.ExpenseRequest;
import com.expensetracker.dto.expense.ExpenseResponse;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final FileUploadUtil fileUploadUtil;
    private final BudgetService budgetService;
    
    @Transactional
    @CacheEvict(value = "expenses", key = "#userId")
    public ExpenseResponse createExpense(String userId, ExpenseRequest request) {
        // Validate category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategoryId(request.getCategoryId());
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setMerchant(request.getMerchant());
        expense.setNotes(request.getNotes());
        expense.setTags(request.getTags());
        expense.setRecurring(request.isRecurring());
        expense.setRecurringFrequency(request.getRecurringFrequency());
        expense.setRecurringEndDate(request.getRecurringEndDate());
        
        expense = expenseRepository.save(expense);
        log.info("Expense created: {} for user: {}", expense.getId(), userId);
        
        // Update budget spent amount
        budgetService.updateBudgetSpentAmount(userId, request.getCategoryId(), request.getDate());
        
        return mapToResponse(expense, category.getName());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "expenses", key = "#userId + '-' + #page + '-' + #size")
    public PagedResponse<ExpenseResponse> getAllExpenses(String userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Expense> expensePage = expenseRepository.findByUserId(userId, pageable);
        
        List<ExpenseResponse> content = expensePage.getContent().stream()
                .map(expense -> {
                    String categoryName = getCategoryName(expense.getCategoryId());
                    return mapToResponse(expense, categoryName);
                })
                .collect(Collectors.toList());
        
        return PagedResponse.<ExpenseResponse>builder()
                .content(content)
                .pageNumber(expensePage.getNumber())
                .pageSize(expensePage.getSize())
                .totalElements(expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .last(expensePage.isLast())
                .first(expensePage.isFirst())
                .build();
    }
    
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(String userId, String expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Check if expense belongs to user
        if (!expense.getUserId().equals(userId)) {
            throw new BadRequestException("You don't have access to this expense");
        }
        
        String categoryName = getCategoryName(expense.getCategoryId());
        return mapToResponse(expense, categoryName);
    }
    
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getExpensesByDateRange(
            String userId, LocalDate startDate, LocalDate endDate, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Expense> expensePage = expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
        
        List<ExpenseResponse> content = expensePage.getContent().stream()
                .map(expense -> {
                    String categoryName = getCategoryName(expense.getCategoryId());
                    return mapToResponse(expense, categoryName);
                })
                .collect(Collectors.toList());
        
        return PagedResponse.<ExpenseResponse>builder()
                .content(content)
                .pageNumber(expensePage.getNumber())
                .pageSize(expensePage.getSize())
                .totalElements(expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .last(expensePage.isLast())
                .first(expensePage.isFirst())
                .build();
    }
    
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getExpensesByCategory(
            String userId, String categoryId, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Expense> expensePage = expenseRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        
        String categoryName = getCategoryName(categoryId);
        List<ExpenseResponse> content = expensePage.getContent().stream()
                .map(expense -> mapToResponse(expense, categoryName))
                .collect(Collectors.toList());
        
        return PagedResponse.<ExpenseResponse>builder()
                .content(content)
                .pageNumber(expensePage.getNumber())
                .pageSize(expensePage.getSize())
                .totalElements(expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .last(expensePage.isLast())
                .first(expensePage.isFirst())
                .build();
    }
    
    @Transactional
    @CacheEvict(value = "expenses", key = "#userId")
    public ExpenseResponse updateExpense(String userId, String expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Check if expense belongs to user
        if (!expense.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot update this expense");
        }
        
        // Validate category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        
        expense.setCategoryId(request.getCategoryId());
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setMerchant(request.getMerchant());
        expense.setNotes(request.getNotes());
        expense.setTags(request.getTags());
        expense.setRecurring(request.isRecurring());
        expense.setRecurringFrequency(request.getRecurringFrequency());
        expense.setRecurringEndDate(request.getRecurringEndDate());
        
        expense = expenseRepository.save(expense);
        log.info("Expense updated: {} for user: {}", expense.getId(), userId);
        
        // Update budget spent amount
        budgetService.updateBudgetSpentAmount(userId, request.getCategoryId(), request.getDate());
        
        return mapToResponse(expense, category.getName());
    }
    
    @Transactional
    @CacheEvict(value = "expenses", key = "#userId")
    public void deleteExpense(String userId, String expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Check if expense belongs to user
        if (!expense.getUserId().equals(userId)) {
            throw new BadRequestException("You cannot delete this expense");
        }
        
        // Delete receipt files
        for (String receiptUrl : expense.getReceiptUrls()) {
            fileUploadUtil.deleteFile(receiptUrl);
        }
        
        expenseRepository.delete(expense);
        log.info("Expense deleted: {} for user: {}", expenseId, userId);
        
        // Update budget spent amount
        budgetService.updateBudgetSpentAmount(userId, expense.getCategoryId(), expense.getDate());
    }
    
    @Transactional
    @CacheEvict(value = "expenses", key = "#userId")
    public ExpenseResponse uploadReceipt(String userId, String expenseId, MultipartFile file) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Check if expense belongs to user
        if (!expense.getUserId().equals(userId)) {
            throw new BadRequestException("You don't have access to this expense");
        }
        
        // Upload file to S3
        String fileUrl = fileUploadUtil.uploadFile(file, "receipts/" + userId);
        
        // Add receipt URL to expense
        List<String> receiptUrls = expense.getReceiptUrls();
        if (receiptUrls == null) {
            receiptUrls = new ArrayList<>();
        }
        receiptUrls.add(fileUrl);
        expense.setReceiptUrls(receiptUrls);
        
        expense = expenseRepository.save(expense);
        log.info("Receipt uploaded for expense: {}", expenseId);
        
        String categoryName = getCategoryName(expense.getCategoryId());
        return mapToResponse(expense, categoryName);
    }
    
    private String getCategoryName(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Unknown");
    }
    
    private ExpenseResponse mapToResponse(Expense expense, String categoryName) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .userId(expense.getUserId())
                .categoryId(expense.getCategoryId())
                .categoryName(categoryName)
                .amount(expense.getAmount())
                .currency(expense.getCurrency())
                .description(expense.getDescription())
                .date(expense.getDate())
                .paymentMethod(expense.getPaymentMethod())
                .merchant(expense.getMerchant())
                .notes(expense.getNotes())
                .tags(expense.getTags())
                .receiptUrls(expense.getReceiptUrls())
                .recurring(expense.isRecurring())
                .recurringFrequency(expense.getRecurringFrequency())
                .recurringEndDate(expense.getRecurringEndDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
