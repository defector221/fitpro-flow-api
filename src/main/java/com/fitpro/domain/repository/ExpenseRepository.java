package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByGymIdOrderByExpenseDateDesc(UUID gymId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.gymId = :gymId " +
           "AND e.expenseDate >= :from AND e.expenseDate <= :to")
    BigDecimal sumByDateRange(@Param("gymId") UUID gymId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
