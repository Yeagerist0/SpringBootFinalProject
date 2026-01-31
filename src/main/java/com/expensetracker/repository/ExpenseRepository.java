package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    
    Page<Expense> findByUserId(String userId, Pageable pageable);
    
    Page<Expense> findByUserIdAndCategoryId(String userId, String categoryId, Pageable pageable);
    
    Page<Expense> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    @Query("{ 'userId': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    List<Expense> findExpensesByUserAndDateRange(String userId, LocalDate startDate, LocalDate endDate);
    
    @Query("{ 'userId': ?0, 'categoryId': ?1, 'date': { $gte: ?2, $lte: ?3 } }")
    List<Expense> findExpensesByUserCategoryAndDateRange(String userId, String categoryId, LocalDate startDate, LocalDate endDate);
    
    @Query("{ 'userId': ?0, 'amount': { $gte: ?1, $lte: ?2 } }")
    Page<Expense> findByUserIdAndAmountBetween(String userId, BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    
    @Query("{ 'userId': ?0, 'tags': { $in: ?1 } }")
    Page<Expense> findByUserIdAndTagsIn(String userId, List<String> tags, Pageable pageable);
    
    @Query("{ 'userId': ?0, 'paymentMethod': ?1 }")
    Page<Expense> findByUserIdAndPaymentMethod(String userId, String paymentMethod, Pageable pageable);
    
    long countByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    
    void deleteAllByUserId(String userId);
}
