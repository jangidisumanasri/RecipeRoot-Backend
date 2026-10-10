package com.reciperoot.backend.controller;

import com.reciperoot.backend.model.MealPlan;
import com.reciperoot.backend.repository.MealPlanRepository;
import com.reciperoot.backend.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanRepository plans;
    private final TokenService tokens;

    private static final Set<String> MEAL_TYPES =
        Set.of("BREAKFAST", "LUNCH", "DINNER", "SNACK");

    public MealPlanController(
            MealPlanRepository plans, TokenService tokens) {
        this.plans = plans;
        this.tokens = tokens;
    }

    private TokenService.SessionUser currentUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return tokens.findUser(authorization.substring(7));
    }

    @GetMapping("/week")
    public ResponseEntity<?> getWeek(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestParam(required = false) LocalDate start) {

        var user = currentUser(authorization);
        if (user == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Please log in."));
        }

        LocalDate firstDay = start == null
            ? LocalDate.now().with(DayOfWeek.MONDAY)
            : start;

        LocalDate lastDay = firstDay.plusDays(6);

        return ResponseEntity.ok(
            plans.findByUserIdAndPlanDateBetweenOrderByPlanDate(
                user.id(), firstDay, lastDay
            )
        );
    }

    @PostMapping
    public ResponseEntity<?> addMeal(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody MealRequest request) {

        var user = currentUser(authorization);
        if (user == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Please log in."));
        }

        if (request.recipeId() == null || request.planDate() == null
                || request.mealType() == null
                || !MEAL_TYPES.contains(request.mealType().toUpperCase())) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide a recipe, date, and valid meal type.")
            );
        }

        MealPlan plan = new MealPlan();
        plan.setUserId(user.id());
        plan.setRecipeId(request.recipeId());
        plan.setPlanDate(request.planDate());
        plan.setMealType(request.mealType().toUpperCase());

        return ResponseEntity.ok(plans.save(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeal(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @PathVariable Long id) {

        var user = currentUser(authorization);
        if (user == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Please log in."));
        }

        var plan = plans.findByIdAndUserId(id, user.id());
        if (plan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        plans.delete(plan.get());
        return ResponseEntity.noContent().build();
    }

    public record MealRequest(
        Long recipeId, LocalDate planDate, String mealType
    ) {}
}