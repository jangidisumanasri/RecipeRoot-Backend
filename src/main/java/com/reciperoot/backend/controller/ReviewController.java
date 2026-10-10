package com.reciperoot.backend.controller;

import com.reciperoot.backend.model.RecipeReview;
import com.reciperoot.backend.repository.AppUserRepository;
import com.reciperoot.backend.repository.RecipeReviewRepository;
import com.reciperoot.backend.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recipes/{recipeId}/reviews")
public class ReviewController {

    private final RecipeReviewRepository reviews;
    private final AppUserRepository users;
    private final TokenService tokens;

    public ReviewController(
            RecipeReviewRepository reviews,
            AppUserRepository users,
            TokenService tokens) {
        this.reviews = reviews;
        this.users = users;
        this.tokens = tokens;
    }

    @GetMapping
    public ResponseEntity<?> getReviews(@PathVariable Long recipeId) {
        return ResponseEntity.ok(Map.of(
            "reviews", reviews.findByRecipeIdOrderByCreatedAtDesc(recipeId),
            "averageRating", reviews.getAverageRating(recipeId),
            "count", reviews.countByRecipeId(recipeId)
        ));
    }

    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @PathVariable Long recipeId,
            @RequestBody ReviewRequest request) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Log in to submit a review."));
        }

        var user = tokens.findUser(authorization.substring(7));
        if (user == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Your login has expired. Log in again."));
        }

        if (request.rating() == null
                || request.rating() < 1 || request.rating() > 5) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Rating must be between 1 and 5.")
            );
        }

        var existing = reviews.findByUserIdAndRecipeId(user.id(), recipeId);
        RecipeReview review = existing.orElseGet(RecipeReview::new);

        review.setUserId(user.id());
        review.setRecipeId(recipeId);
        review.setRating(request.rating());
        review.setComment(request.comment() == null
            ? "" : request.comment().trim());

        return ResponseEntity.ok(reviews.save(review));
    }

    public record ReviewRequest(Integer rating, String comment) {}
}