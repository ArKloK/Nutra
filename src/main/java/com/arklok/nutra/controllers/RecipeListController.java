package com.arklok.nutra.controllers;

import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Recipe;
import com.arklok.nutra.services.RecipeService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RecipeListController implements IController {

    private static final Logger log = LoggerFactory.getLogger(RecipeListController.class);
    private HomeController homeController;

    @FXML
    private TextField searchField;

    @FXML
    private VBox recipeListContainer;

    private final RecipeService recipeService;

    public RecipeListController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public void initialize() {
        log.info("Recipe list controller initialized");

        // Load all recipes initially
        loadRecipes();

        // Setup search filter
        searchField.textProperty().addListener((_, _, newValue) -> filterRecipes(newValue));
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Load all recipes and display them as cards
     */
    private void loadRecipes() {
        try {
            List<Recipe> recipes = recipeService.findAllWithDetails();
            displayRecipes(recipes);
        } catch (Exception e) {
            log.error("Error loading recipes", e);
        }
    }

    /**
     * Filter recipes by search term
     */
    private void filterRecipes(String searchTerm) {
        try {
            List<Recipe> recipes;
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                recipes = recipeService.findAllWithDetails();
            } else {
                recipes = recipeService.searchByTitleWithDetails(searchTerm);
            }
            displayRecipes(recipes);
        } catch (Exception e) {
            log.error("Error filtering recipes", e);
        }
    }

    /**
     * Display recipes as cards
     */
    private void displayRecipes(List<Recipe> recipes) {
        recipeListContainer.getChildren().clear();

        if (recipes == null || recipes.isEmpty()) {
            // Show empty state
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(40));

            Text emptyText = new Text("No se encontraron recetas");
            emptyText.setStyle("-fx-font-size: 16px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

            emptyState.getChildren().add(emptyText);
            recipeListContainer.getChildren().add(emptyState);
            return;
        }

        // Create a card for each recipe
        for (Recipe recipe : recipes) {
            VBox recipeCard = createRecipeCard(recipe);
            recipeListContainer.getChildren().add(recipeCard);
        }
    }

    /**
     * Create a card for a recipe
     */
    private VBox createRecipeCard(Recipe recipe) {
        VBox card = new VBox(15);
        card.getStyleClass().add("patient-card");
        card.setPadding(new Insets(20));

        // Header with name
        Text nameText = new Text(recipe.getTitle());
        nameText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

        // Recipe info section
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(10, 0, 0, 0));

        // Description
        if (recipe.getDescription() != null && !recipe.getDescription().isEmpty()) {
            Text descText = new Text(recipe.getDescription());
            descText.setStyle("-fx-font-size: 13px; -fx-fill: " + UIConstants.COLOR_SECONDARY + ";");
            descText.setWrappingWidth(1200);
            infoBox.getChildren().add(descText);
        }

        // Time
        if (recipe.getTimeMinutes() != null) {
            HBox timeRow = createInfoRow("Tiempo de preparación:", recipe.getTimeMinutes() + " minutos");
            infoBox.getChildren().add(timeRow);
        }

        // Ingredients count
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            HBox ingredientsRow = createInfoRow("Ingredientes:", recipe.getIngredients().size() + " ingredientes");
            infoBox.getChildren().add(ingredientsRow);
        }

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button viewButton = new Button("Ver Detalles");
        viewButton.getStyleClass().add("secondary-button");
        viewButton.setOnAction(_ -> viewRecipeDetails(recipe));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("save-button");
        editButton.setOnAction(_ -> editRecipe(recipe));

        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("cancel-button");
        deleteButton.setOnAction(_ -> confirmDeleteRecipe(recipe));

        buttonBox.getChildren().addAll(viewButton, editButton, deleteButton);

        card.getChildren().addAll(nameText, infoBox, buttonBox);

        return card;
    }

    /**
     * Create an info row with label and value
     */
    private HBox createInfoRow(String label, String value) {
        return IngredientListController.gethBox(label, value);
    }

    /**
     * View recipe details
     */
    private void viewRecipeDetails(Recipe recipe) {
        log.info("Viewing recipe details: {}", recipe.getTitle());

        try {
            // Load recipe with all details
            Recipe fullRecipe = recipeService.findByIdWithDetails(recipe.getId()).orElse(recipe);

            if (homeController != null) {
                homeController.showRecipeDetail(fullRecipe, this);
            } else {
                log.error("HomeController is null! Cannot show recipe details.");
            }
        } catch (Exception e) {
            log.error("Error viewing recipe details", e);
            showError("Error al ver detalles de receta", e.getMessage());
        }
    }

    /**
     * Edit an existing recipe
     */
    private void editRecipe(Recipe recipe) {
        log.info("Editing recipe: {}", recipe.getTitle());

        try {
            // Load recipe with all details
            Recipe fullRecipe = recipeService.findByIdWithDetails(recipe.getId()).orElse(recipe);

            if (homeController != null) {
                homeController.showEditRecipe(fullRecipe);
            } else {
                log.error("HomeController is null! Cannot edit recipe.");
            }
        } catch (Exception e) {
            log.error("Error editing recipe", e);
            showError("Error al editar receta", e.getMessage());
        }
    }

    /**
     * Confirm and delete a recipe
     */
    private void confirmDeleteRecipe(Recipe recipe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar receta?");
        alert.setContentText("¿Estás seguro de que quieres eliminar la receta '" + recipe.getTitle() + "'?\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteRecipe(recipe);
        }
    }

    /**
     * Delete a recipe
     */
    private void deleteRecipe(Recipe recipe) {
        try {
            recipeService.deleteById(recipe.getId());
            log.info("Recipe deleted: {}", recipe.getTitle());
            loadRecipes();
        } catch (Exception e) {
            log.error("Error deleting recipe", e);
            showError("Error al eliminar receta", e.getMessage());
        }
    }

    /**
     * Show new recipe form
     */
    @FXML
    public void showNewRecipe() {
        log.info("Show new recipe button clicked");
        if (homeController != null) {
            homeController.showNewRecipe();
        } else {
            log.error("HomeController is null! Cannot show new recipe form.");
        }
    }

    /**
     * Go back to calendar view
     */
    @FXML
    public void goBack() {
        log.info("Go back button clicked");
        if (homeController != null) {
            homeController.showCalendarView();
        } else {
            log.error("HomeController is null! Cannot go back.");
        }
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

