
package com.reciperoot.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String imageUrl;

    private String category;

    private Integer cookingTime;

    private Integer servings;

    @OneToMany(
        mappedBy = "recipe",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<RecipeIngredient> ingredientItems = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String instructions;

    public Recipe() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    @JsonProperty("ingredients")
    public List<String> getIngredients() {
        return ingredientItems.stream()
            .map(RecipeIngredient::getIngredient)
            .toList();
    }

    @JsonProperty("ingredients")
    public void setIngredients(List<String> ingredients) {
        ingredientItems.clear();

        if (ingredients != null) {
            for (String ingredient : ingredients) {
                if (ingredient != null && !ingredient.isBlank()) {
                    ingredientItems.add(
                        new RecipeIngredient(this, ingredient.trim())
                    );
                }
            }
        }
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}