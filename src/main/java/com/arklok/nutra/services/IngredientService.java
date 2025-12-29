package com.arklok.nutra.services;

import com.arklok.nutra.models.Ingredient;
import com.arklok.nutra.repositories.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Save an ingredient (create or update)
     */
    public Ingredient save(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }

        // Ensure collections are not null
        if (ingredient.getVitamins() == null) {
            ingredient.setVitamins(new java.util.HashMap<>());
        }
        if (ingredient.getMinerals() == null) {
            ingredient.setMinerals(new java.util.HashMap<>());
        }
        if (ingredient.getAllergens() == null) {
            ingredient.setAllergens(new java.util.HashSet<>());
        }

        return ingredientRepository.save(ingredient);
    }

    /**
     * Find an ingredient by id
     */
    public Optional<Ingredient> findById(Long id) {
        return ingredientRepository.findById(id);
    }

    /**
     * Find an ingredient by id with all collections loaded
     */
    public Optional<Ingredient> findByIdWithDetails(Long id) {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findById(id);
        ingredientOpt.ifPresent(ingredient -> {
            // Force load lazy collections within transaction
            ingredient.getVitamins().size();
            ingredient.getMinerals().size();
            ingredient.getAllergens().size();
        });
        return ingredientOpt;
    }

    /**
     * Find all ingredients
     */
    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    /**
     * Find all ingredients with all collections loaded
     */
    public List<Ingredient> findAllWithDetails() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        // Force load lazy collections within transaction
        for (Ingredient ingredient : ingredients) {
            ingredient.getVitamins().size();
            ingredient.getMinerals().size();
            ingredient.getAllergens().size();
        }
        return ingredients;
    }

    /**
     * Search ingredients by name
     */
    public List<Ingredient> searchByName(String searchTerm) {
        return ingredientRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    /**
     * Search ingredients by name with all collections loaded
     */
    public List<Ingredient> searchByNameWithDetails(String searchTerm) {
        List<Ingredient> ingredients = ingredientRepository.findByNameContainingIgnoreCase(searchTerm);
        // Force load lazy collections within transaction
        for (Ingredient ingredient : ingredients) {
            ingredient.getVitamins().size();
            ingredient.getMinerals().size();
            ingredient.getAllergens().size();
        }
        return ingredients;
    }

    /**
     * Find ingredient by exact name
     */
    public Ingredient findByName(String name) {
        return ingredientRepository.findByNameIgnoreCase(name);
    }

    /**
     * Delete an ingredient
     */
    public void delete(Ingredient ingredient) {
        ingredientRepository.delete(ingredient);
    }

    /**
     * Delete an ingredient by id
     */
    public void deleteById(Long id) {
        ingredientRepository.deleteById(id);
    }

    /**
     * Check if an ingredient exists by id
     */
    public boolean existsById(Long id) {
        return ingredientRepository.existsById(id);
    }

    /**
     * Count total ingredients
     */
    public long count() {
        return ingredientRepository.count();
    }
}

