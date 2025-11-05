package com.arklok.nutra.models;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double calories;

    private Double sugar;

    private Double carbohydrates;

    private Double protein;

    private Double fiber;

    private Double fat;

    private Double unsaturatedFat;

    private Double saturatedFat;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ingredient_vitamins", joinColumns = @JoinColumn(name = "ingredient_id"))
    @MapKeyColumn(name = "vitamin_name")
    @Column(name = "vitamin_value")
    private Map<String, Float> vitamins = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ingredient_minerals", joinColumns = @JoinColumn(name = "ingredient_id"))
    @MapKeyColumn(name = "mineral_name")
    @Column(name = "mineral_value")
    private Map<String, Float> minerals = new HashMap<>();

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    public Ingredient() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }

    public Double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(Double carbohydrates) { this.carbohydrates = carbohydrates; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }

    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }

    public Double getUnsaturatedFat() { return unsaturatedFat; }
    public void setUnsaturatedFat(Double unsaturatedFat) { this.unsaturatedFat = unsaturatedFat; }

    public Double getSaturatedFat() { return saturatedFat; }
    public void setSaturatedFat(Double saturatedFat) { this.saturatedFat = saturatedFat; }

    public Map<String, Float> getVitamins() { return vitamins; }
    public void setVitamins(Map<String, Float> vitamins) { this.vitamins = vitamins; }

    public Map<String, Float> getMinerals() { return minerals; }
    public void setMinerals(Map<String, Float> minerals) { this.minerals = minerals; }

    public Set<RecipeIngredient> getRecipeIngredients() { return recipeIngredients; }
    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) { this.recipeIngredients = recipeIngredients; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return name != null ? name : "Ingrediente sin nombre";
    }
}
