package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Member;
import com.fitpro.domain.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Page<Member> findByGymId(UUID gymId, Pageable pageable);

    @Query("""
            SELECT m FROM Member m WHERE m.gymId = :gymId
            AND (:search IS NULL OR LOWER(m.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR m.phone LIKE CONCAT('%', :search, '%')
                OR m.memberCode LIKE CONCAT('%', :search, '%'))
            AND (:status IS NULL OR m.status = :status)
            """)
    Page<Member> search(
            @Param("gymId") UUID gymId,
            @Param("search") String search,
            @Param("status") MemberStatus status,
            Pageable pageable);

    Optional<Member> findByGymIdAndMemberCode(UUID gymId, String memberCode);

    long countByGymIdAndCreatedAtAfter(UUID gymId, Instant after);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.gymId = :gymId AND m.dateOfBirth IS NOT NULL " +
           "AND EXTRACT(MONTH FROM m.dateOfBirth) = :month AND EXTRACT(DAY FROM m.dateOfBirth) BETWEEN :dayStart AND :dayEnd")
    long countBirthdaysInRange(@Param("gymId") UUID gymId, @Param("month") int month,
                               @Param("dayStart") int dayStart, @Param("dayEnd") int dayEnd);
}
