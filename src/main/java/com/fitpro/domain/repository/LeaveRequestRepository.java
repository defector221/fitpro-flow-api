package com.fitpro.domain.repository;

import com.fitpro.domain.entity.LeaveRequest;
import com.fitpro.domain.enums.LeaveRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    @Query("SELECT lr FROM LeaveRequest lr JOIN Employee e ON lr.employeeId = e.id " +
           "WHERE e.gymId = :gymId ORDER BY lr.createdAt DESC")
    Page<LeaveRequest> findByGym(@Param("gymId") UUID gymId, Pageable pageable);

    @Query("SELECT COUNT(lr) FROM LeaveRequest lr JOIN Employee e ON lr.employeeId = e.id " +
           "WHERE e.gymId = :gymId AND lr.status = :status")
    long countByGymAndStatus(@Param("gymId") UUID gymId, @Param("status") LeaveRequestStatus status);
}
