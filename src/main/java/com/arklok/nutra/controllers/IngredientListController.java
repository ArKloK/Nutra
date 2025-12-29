package com.arklok.nutra.controllers;

import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Ingredient;
import com.arklok.nutra.services.IngredientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IngredientListController implements IController {

    private static final Logger log = LoggerFactory.getLogger(IngredientListController.class);
    private HomeController homeController;

    @FXML
    private TextField searchField;

    @FXML
    private VBox ingredientListContainer;

    private final IngredientService ingredientService;
    private final ApplicationContext applicationContext;

    public IngredientListController(IngredientService ingredientService,
                                   ApplicationContext applicationContext) {
        this.ingredientService = ingredientService;
        this.applicationContext = applicationContext;
    }

    public void initialize() {
        log.info("Ingredient list controller initialized");

        // Load all ingredients initially
        loadIngredients();

        // Setup search filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterIngredients(newValue));
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Load all ingredients and display them as cards
     */
    private void loadIngredients() {
        try {
            List<Ingredient> ingredients = ingredientService.findAllWithDetails();
            displayIngredients(ingredients);
        } catch (Exception e) {
            log.error("Error loading ingredients", e);
        }
    }

    /**
     * Filter ingredients by search term
     */
    private void filterIngredients(String searchTerm) {
        try {
            List<Ingredient> ingredients;
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                ingredients = ingredientService.findAllWithDetails();
            } else {
                ingredients = ingredientService.searchByNameWithDetails(searchTerm);
            }
            displayIngredients(ingredients);
        } catch (Exception e) {
            log.error("Error filtering ingredients", e);
        }
    }

    /**
     * Display ingredients as cards
     */
    private void displayIngredients(List<Ingredient> ingredients) {
        ingredientListContainer.getChildren().clear();

        if (ingredients == null || ingredients.isEmpty()) {
            // Show empty state
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(40));

            Text emptyText = new Text("No se encontraron ingredientes");
            emptyText.setStyle("-fx-font-size: 16px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

            emptyState.getChildren().add(emptyText);
            ingredientListContainer.getChildren().add(emptyState);
            return;
        }

        // Create a card for each ingredient
        for (Ingredient ingredient : ingredients) {
            VBox ingredientCard = createIngredientCard(ingredient);
            ingredientListContainer.getChildren().add(ingredientCard);
        }
    }

    /**
     * Create a card for an ingredient
     */
    private VBox createIngredientCard(Ingredient ingredient) {
        VBox card = new VBox(15);
        card.getStyleClass().add("patient-card");
        card.setPadding(new Insets(20));

        // Header with name
        Text nameText = new Text(ingredient.getName());
        nameText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

        // Nutritional info section
        VBox nutritionBox = new VBox(8);
        nutritionBox.setPadding(new Insets(10, 0, 0, 0));

        // Calories
        if (ingredient.getCalories() != null) {
            HBox caloriesRow = createInfoRow("Calorías:", String.format("%.1f kcal", ingredient.getCalories()));
            nutritionBox.getChildren().add(caloriesRow);
        }

        // Protein
        if (ingredient.getProtein() != null) {
            HBox proteinRow = createInfoRow("Proteínas:", String.format("%.1f g", ingredient.getProtein()));
            nutritionBox.getChildren().add(proteinRow);
        }

        // Carbohydrates
        if (ingredient.getCarbohydrates() != null) {
            HBox carbsRow = createInfoRow("Carbohidratos:", String.format("%.1f g", ingredient.getCarbohydrates()));
            nutritionBox.getChildren().add(carbsRow);
        }

        // Fat
        if (ingredient.getFat() != null) {
            HBox fatRow = createInfoRow("Grasas:", String.format("%.1f g", ingredient.getFat()));
            nutritionBox.getChildren().add(fatRow);
        }

        // Allergens
        if (ingredient.getAllergens() != null && !ingredient.getAllergens().isEmpty()) {
            String allergenList = ingredient.getAllergens().stream()
                    .map(Enum::toString)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            HBox allergenRow = createInfoRow("Alérgenos:", allergenList);
            nutritionBox.getChildren().add(allergenRow);
        }

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("save-button");
        editButton.setOnAction(_ -> editIngredient(ingredient));

        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("cancel-button");
        deleteButton.setOnAction(_ -> confirmDeleteIngredient(ingredient));

        buttonBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameText, nutritionBox, buttonBox);

        return card;
    }

    /**
     * Create an info row with label and value
     */
    private HBox createInfoRow(String label, String value) {
        return gethBox(label, value);
    }

    @NonNull
    static HBox gethBox(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Text labelText = new Text(label);
        labelText.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

        Text valueText = new Text(value);
        valueText.setStyle("-fx-font-size: 13px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

        row.getChildren().addAll(labelText, valueText);

        return row;
    }

    /**
     * Edit an existing ingredient
     */
    private void editIngredient(Ingredient ingredient) {
        log.info("Editing ingredient: {}", ingredient.getName());

        try {
            // Load ingredient with all details
            Ingredient fullIngredient = ingredientService.findByIdWithDetails(ingredient.getId()).orElse(ingredient);

            // Load ingredient form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ingredient.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            IngredientController controller = loader.getController();
            controller.setIngredientForEdit(fullIngredient);

            // Create and show dialog
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Editar Ingrediente");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            // Refresh list if ingredient was saved
            if (controller.isSaved()) {
                loadIngredients();
            }
        } catch (Exception e) {
            log.error("Error editing ingredient", e);
            showError("Error al editar ingrediente", e.getMessage());
        }
    }

    /**
     * Confirm and delete an ingredient
     */
    private void confirmDeleteIngredient(Ingredient ingredient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar ingrediente?");
        alert.setContentText("¿Estás seguro de que quieres eliminar el ingrediente '" + ingredient.getName() + "'?\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteIngredient(ingredient);
        }
    }

    /**
     * Delete an ingredient
     */
    private void deleteIngredient(Ingredient ingredient) {
        try {
            ingredientService.deleteById(ingredient.getId());
            log.info("Ingredient deleted: {}", ingredient.getName());
            loadIngredients();
        } catch (Exception e) {
            log.error("Error deleting ingredient", e);
            showError("Error al eliminar ingrediente", e.getMessage());
        }
    }

    /**
     * Show new ingredient form
     */
    @FXML
    public void showNewIngredient() {
        log.info("Show new ingredient button clicked");

        try {
            // Load ingredient form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ingredient.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            IngredientController controller = loader.getController();

            // Create and show dialog
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nuevo Ingrediente");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            // Refresh list if ingredient was saved
            if (controller.isSaved()) {
                loadIngredients();
            }
        } catch (Exception e) {
            log.error("Error showing new ingredient form", e);
            showError("Error al abrir formulario", e.getMessage());
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

