package com.arklok.nutra.repositories;

import com.arklok.nutra.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Find ingredients by name containing the given text (case-insensitive)
     */
    List<Ingredient> findByNameContainingIgnoreCase(String name);

    /**
     * Find ingredient by exact name (case-insensitive)
     */
    Ingredient findByNameIgnoreCase(String name);
}

