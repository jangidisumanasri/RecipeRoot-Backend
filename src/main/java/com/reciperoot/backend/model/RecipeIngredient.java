package com.reciperoot.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "ingredient", nullable = false)
    private String ingredient;

    public RecipeIngredient() {
    }

    public RecipeIngredient(Recipe recipe, String ingredient) {
        this.recipe = recipe;
        this.ingredient = ingredient;
    }

    public Long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}