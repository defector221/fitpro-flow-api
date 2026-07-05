package com.fitpro.domain.repository;

import com.fitpro.domain.entity.EmployeeAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, UUID> {

    @Query("SELECT ea FROM EmployeeAttendance ea JOIN Employee e ON ea.employeeId = e.id " +
           "WHERE e.gymId = :gymId AND ea.attendanceDate = :date")
    List<EmployeeAttendance> findByGymAndDate(@Param("gymId") UUID gymId, @Param("date") LocalDate date);

    @Query("SELECT ea FROM EmployeeAttendance ea JOIN Employee e ON ea.employeeId = e.id " +
           "WHERE e.gymId = :gymId ORDER BY ea.attendanceDate DESC")
    Page<EmployeeAttendance> findByGym(@Param("gymId") UUID gymId, Pageable pageable);

    Optional<EmployeeAttendance> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate date);
}
