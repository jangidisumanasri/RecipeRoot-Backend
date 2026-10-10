package com.reciperoot.backend.repository;

import com.reciperoot.backend.model.RecipeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RecipeReviewRepository
        extends JpaRepository<RecipeReview, Long> {

    List<RecipeReview> findByRecipeIdOrderByCreatedAtDesc(Long recipeId);

    Optional<RecipeReview> findByUserIdAndRecipeId(
        Long userId, Long recipeId
    );

    @Query("""
        SELECT COALESCE(AVG(r.rating), 0)
        FROM RecipeReview r
        WHERE r.recipeId = :recipeId
    """)
    Double getAverageRating(@Param("recipeId") Long recipeId);

    long countByRecipeId(Long recipeId);
}