package com.fitpro.domain.repository;

import com.fitpro.domain.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {

    List<MembershipPlan> findByGymIdAndActiveTrueOrderByPriceAsc(UUID gymId);

    List<MembershipPlan> findByGymIdOrderByPriceAsc(UUID gymId);
}
