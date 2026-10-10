package com.reciperoot.backend.repository;

import com.reciperoot.backend.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUserIdAndPlanDateBetweenOrderByPlanDate(
        Long userId, LocalDate start, LocalDate end
    );

    Optional<MealPlan> findByIdAndUserId(Long id, Long userId);
}