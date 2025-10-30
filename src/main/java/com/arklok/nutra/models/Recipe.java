package com.arklok.nutra.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private Integer timeMinutes;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    public Recipe() {}

    public void addIngredient(RecipeIngredient ri) {
        ingredients.add(ri);
        ri.setRecipe(this);
    }

    public void removeIngredient(RecipeIngredient ri) {
        ingredients.remove(ri);
        ri.setRecipe(null);
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public Integer getTimeMinutes() { return timeMinutes; }
    public void setTimeMinutes(Integer timeMinutes) { this.timeMinutes = timeMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Set<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(Set<RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe recipe)) return false;
        return id != null && id.equals(recipe.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
