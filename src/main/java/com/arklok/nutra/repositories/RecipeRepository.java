package com.arklok.nutra.repositories;

import com.arklok.nutra.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Find recipes by title containing the given text (case-insensitive)
     */
    List<Recipe> findByTitleContainingIgnoreCase(String title);

    /**
     * Find recipe by exact title (case-insensitive)
     */
    Recipe findByTitleIgnoreCase(String title);
}

