package com.reciperoot.backend.controller;

import com.reciperoot.backend.model.Recipe;
import com.reciperoot.backend.repository.RecipeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://127.0.0.1:5173"
})
public class RecipeController {

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Get all recipes
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Get one recipe by ID
    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return recipeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recipe not found"
            ));
    }

    // Add a recipe
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        recipe.setId(null);
        return recipeRepository.save(recipe);
    }

    // Update a recipe
    @PutMapping("/{id}")
    public Recipe updateRecipe(
            @PathVariable Long id,
            @RequestBody Recipe updatedRecipe) {

        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recipe not found"
            ));

        recipe.setTitle(updatedRecipe.getTitle());
        recipe.setDescription(updatedRecipe.getDescription());
        recipe.setImageUrl(updatedRecipe.getImageUrl());
        recipe.setCategory(updatedRecipe.getCategory());
        recipe.setCookingTime(updatedRecipe.getCookingTime());
        recipe.setServings(updatedRecipe.getServings());
        recipe.setIngredients(updatedRecipe.getIngredients());
        recipe.setInstructions(updatedRecipe.getInstructions());

        return recipeRepository.save(recipe);
    }

    // Delete a recipe
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recipe not found"
            );
        }

        recipeRepository.deleteById(id);
    }
}