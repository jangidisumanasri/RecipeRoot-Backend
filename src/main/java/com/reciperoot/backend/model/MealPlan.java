package com.reciperoot.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "meal_plans",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "plan_date", "meal_type"}
    )
)
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "meal_type", nullable = false)
    private String mealType;

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
}