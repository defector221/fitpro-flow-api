package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerRepository extends JpaRepository<Trainer, UUID> {

    @Query("SELECT t FROM Trainer t JOIN Employee e ON t.employeeId = e.id WHERE e.gymId = :gymId AND t.active = true")
    List<Trainer> findByGym(@Param("gymId") UUID gymId);

    Optional<Trainer> findByEmployeeId(UUID employeeId);
}
