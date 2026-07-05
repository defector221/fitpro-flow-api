package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GymRepository extends JpaRepository<Gym, UUID> {}
