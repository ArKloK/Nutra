package com.arklok.nutra.services;

import com.arklok.nutra.models.Recipe;
import com.arklok.nutra.repositories.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Save a recipe (create or update)
     */
    public Recipe save(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }

        // Ensure collections are not null
        if (recipe.getIngredients() == null) {
            recipe.setIngredients(new java.util.HashSet<>());
        }

        return recipeRepository.save(recipe);
    }

    /**
     * Find a recipe by id
     */
    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    /**
     * Find a recipe by id with all ingredients loaded
     */
    public Optional<Recipe> findByIdWithDetails(Long id) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        recipeOpt.ifPresent(recipe -> {
            // Force load lazy collections within transaction
            recipe.getIngredients().size();
            // Also load ingredient details
            recipe.getIngredients().forEach(ri -> {
                ri.getIngredient().getName(); // Force load ingredient
            });
        });
        return recipeOpt;
    }

    /**
     * Find all recipes
     */
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    /**
     * Find all recipes with all ingredients loaded
     */
    public List<Recipe> findAllWithDetails() {
        List<Recipe> recipes = recipeRepository.findAll();
        // Force load lazy collections within transaction
        for (Recipe recipe : recipes) {
            recipe.getIngredients().size();
            // Also load ingredient details
            recipe.getIngredients().forEach(ri -> {
                ri.getIngredient().getName(); // Force load ingredient
            });
        }
        return recipes;
    }

    /**
     * Search recipes by title
     */
    public List<Recipe> searchByTitle(String searchTerm) {
        return recipeRepository.findByTitleContainingIgnoreCase(searchTerm);
    }

    /**
     * Search recipes by title with all ingredients loaded
     */
    public List<Recipe> searchByTitleWithDetails(String searchTerm) {
        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(searchTerm);
        // Force load lazy collections within transaction
        for (Recipe recipe : recipes) {
            recipe.getIngredients().size();
            // Also load ingredient details
            recipe.getIngredients().forEach(ri -> {
                ri.getIngredient().getName(); // Force load ingredient
            });
        }
        return recipes;
    }

    /**
     * Find recipe by exact title
     */
    public Recipe findByTitle(String title) {
        return recipeRepository.findByTitleIgnoreCase(title);
    }

    /**
     * Delete a recipe
     */
    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    /**
     * Delete a recipe by id
     */
    public void deleteById(Long id) {
        recipeRepository.deleteById(id);
    }

    /**
     * Check if a recipe exists by id
     */
    public boolean existsById(Long id) {
        return recipeRepository.existsById(id);
    }

    /**
     * Count total recipes
     */
    public long count() {
        return recipeRepository.count();
    }
}

