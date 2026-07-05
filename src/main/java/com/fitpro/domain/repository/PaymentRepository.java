package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.gymId = :gymId " +
           "AND p.status = 'COMPLETED' AND p.paidAt >= :start AND p.paidAt < :end")
    BigDecimal sumRevenueByDateRange(
            @Param("gymId") UUID gymId,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.gymId = :gymId " +
           "AND p.status = 'PENDING'")
    BigDecimal sumPendingPayments(@Param("gymId") UUID gymId);

    Page<Payment> findByGymIdOrderByPaidAtDesc(UUID gymId, Pageable pageable);
}
