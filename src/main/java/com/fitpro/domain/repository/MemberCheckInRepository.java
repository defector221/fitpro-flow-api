package com.fitpro.domain.repository;

import com.fitpro.domain.entity.MemberCheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberCheckInRepository extends JpaRepository<MemberCheckIn, UUID> {

    @Query("""
        SELECT COUNT(c)
        FROM MemberCheckIn c
        JOIN Member m ON c.memberId = m.id
        WHERE m.gymId = :gymId
          AND c.checkInAt >= :start
          AND c.checkInAt < :end
        """)
    long countByGymAndDateRange(
            @Param("gymId") UUID gymId,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("""
        SELECT c
        FROM MemberCheckIn c
        JOIN Member m ON c.memberId = m.id
        WHERE m.gymId = :gymId
        ORDER BY c.checkInAt DESC
        """)
    Page<MemberCheckIn> findRecentByGym(
            @Param("gymId") UUID gymId,
            Pageable pageable);

    @Query("""
        SELECT c
        FROM MemberCheckIn c
        JOIN Member m ON c.memberId = m.id
        WHERE m.gymId = :gymId
          AND c.checkInAt >= :start
        ORDER BY c.checkInAt DESC
        """)
    List<MemberCheckIn> findTodayByGym(
            @Param("gymId") UUID gymId,
            @Param("start") Instant start);

    @Query("""
        SELECT c
        FROM MemberCheckIn c
        WHERE c.memberId = :memberId
          AND c.checkOutAt IS NULL
        ORDER BY c.checkInAt DESC
        """)
    Optional<MemberCheckIn> findLatestOpenByMemberId(@Param("memberId") UUID memberId);
}
