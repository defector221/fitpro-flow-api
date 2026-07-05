package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Membership;
import com.fitpro.domain.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    List<Membership> findByMemberIdOrderByStartDateDesc(UUID memberId);

    @Query("SELECT COUNT(ms) FROM Membership ms JOIN Member m ON ms.memberId = m.id " +
           "WHERE m.gymId = :gymId AND ms.endDate BETWEEN :from AND :to AND ms.status = :status")
    long countRenewalsDue(
            @Param("gymId") UUID gymId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") MembershipStatus status);
}
