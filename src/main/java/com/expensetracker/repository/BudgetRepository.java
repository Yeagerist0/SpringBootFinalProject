package com.expensetracker.repository;

import com.expensetracker.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    
    List<Budget> findByUserId(String userId);
    
    List<Budget> findByUserIdAndStartDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    
    @Query("{ 'userId': ?0, 'startDate': { $lte: ?1 }, 'endDate': { $gte: ?1 } }")
    List<Budget> findActiveBudgetsForUser(String userId, LocalDate currentDate);
    
    Optional<Budget> findByUserIdAndCategoryIdAndStartDateBetween(String userId, String categoryId, LocalDate startDate, LocalDate endDate);
    
    @Query("{ 'userId': ?0, 'categoryId': ?1, 'startDate': { $lte: ?2 }, 'endDate': { $gte: ?2 } }")
    Optional<Budget> findActiveBudgetForCategory(String userId, String categoryId, LocalDate currentDate);
    
    @Query("{ 'userId': ?0, 'alertEnabled': true, 'alertSent': false }")
    List<Budget> findUnsentAlertBudgets(String userId);
    
    void deleteAllByUserId(String userId);
}
