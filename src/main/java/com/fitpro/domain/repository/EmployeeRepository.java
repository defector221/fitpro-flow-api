package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Employee;
import com.fitpro.domain.enums.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Page<Employee> findByGymId(UUID gymId, Pageable pageable);

    @Query("""
            SELECT e FROM Employee e WHERE e.gymId = :gymId
            AND (:search IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR e.employeeCode LIKE CONCAT('%', :search, '%'))
            AND (:status IS NULL OR e.employmentStatus = :status)
            """)
    Page<Employee> search(
            @Param("gymId") UUID gymId,
            @Param("search") String search,
            @Param("status") EmploymentStatus status,
            Pageable pageable);

    Optional<Employee> findByGymIdAndEmployeeCode(UUID gymId, String employeeCode);

    long countByGymIdAndEmploymentStatus(UUID gymId, EmploymentStatus status);
}
