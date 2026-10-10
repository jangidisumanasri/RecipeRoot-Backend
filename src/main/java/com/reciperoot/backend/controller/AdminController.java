package com.reciperoot.backend.controller;

import com.reciperoot.backend.model.AppUser;
import com.reciperoot.backend.repository.AppUserRepository;
import com.reciperoot.backend.repository.MealPlanRepository;
import com.reciperoot.backend.repository.RecipeReviewRepository;
import com.reciperoot.backend.repository.RecipeRepository;
import com.reciperoot.backend.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AppUserRepository users;
    private final MealPlanRepository plans;
    private final RecipeReviewRepository reviews;
    private final RecipeRepository recipes;
    private final TokenService tokens;

    public AdminController(
            AppUserRepository users,
            MealPlanRepository plans,
            RecipeReviewRepository reviews,
            RecipeRepository recipes,
            TokenService tokens) {
        this.users = users;
        this.plans = plans;
        this.reviews = reviews;
        this.recipes = recipes;
        this.tokens = tokens;
    }

    private TokenService.SessionUser admin(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        var user = tokens.findUser(authorization.substring(7));
        return user != null && "ADMIN".equals(user.role()) ? user : null;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (admin(authorization) == null) {
            return ResponseEntity.status(403)
                .body(Map.of("message", "Admin access required."));
        }

        return ResponseEntity.ok(Map.of(
            "users", users.count(),
            "recipes", recipes.count(),
            "mealPlans", plans.count(),
            "reviews", reviews.count()
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (admin(authorization) == null) {
            return ResponseEntity.status(403)
                .body(Map.of("message", "Admin access required."));
        }

        return ResponseEntity.ok(users.findAll().stream().map(user ->
            Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            )
        ).toList());
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<?> deleteRecipe(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @PathVariable Long id) {

        if (admin(authorization) == null) {
            return ResponseEntity.status(403)
                .body(Map.of("message", "Admin access required."));
        }

        if (!recipes.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        recipes.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @PathVariable Long id) {

        if (admin(authorization) == null) {
            return ResponseEntity.status(403)
                .body(Map.of("message", "Admin access required."));
        }

        if (!reviews.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        reviews.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}