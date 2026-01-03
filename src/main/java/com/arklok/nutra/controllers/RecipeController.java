package com.arklok.nutra.controllers;

import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Ingredient;
import com.arklok.nutra.models.Recipe;
import com.arklok.nutra.models.RecipeIngredient;
import com.arklok.nutra.services.IngredientService;
import com.arklok.nutra.services.RecipeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
public class RecipeController implements IController {

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField timeField;

    @FXML
    private ComboBox<Ingredient> ingredientComboBox;

    @FXML
    private TableView<RecipeIngredientRow> ingredientsTable;

    @FXML
    private TableColumn<RecipeIngredientRow, String> ingredientNameColumn;

    @FXML
    private TableColumn<RecipeIngredientRow, String> amountColumn;

    @FXML
    private TableColumn<RecipeIngredientRow, String> unitColumn;

    @FXML
    private TableColumn<RecipeIngredientRow, String> notesColumn;

    @FXML
    private TextArea instructionsArea;

    @FXML
    private TextArea notesArea;

    private HomeController homeController;
    private final ApplicationContext applicationContext;
    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    private final ObservableList<RecipeIngredientRow> ingredientRows = FXCollections.observableArrayList();
    private final ObservableList<Ingredient> availableIngredients = FXCollections.observableArrayList();

    private Recipe editingRecipe;
    private boolean isEditMode = false;

    public RecipeController(ApplicationContext applicationContext,
                          RecipeService recipeService,
                          IngredientService ingredientService) {
        this.applicationContext = applicationContext;
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    public void initialize() {
        log.info("Recipe controller initialized");
        setupIngredientsTable();
        setupIngredientComboBox();
        loadIngredientsFromDatabase();
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Load ingredients from database
     */
    private void loadIngredientsFromDatabase() {
        try {
            availableIngredients.clear();
            availableIngredients.addAll(ingredientService.findAll());
            log.info("Loaded {} ingredients from database", availableIngredients.size());
        } catch (Exception e) {
            log.error("Error loading ingredients from database", e);
        }
    }

    /**
     * Setup the ingredient ComboBox
     */
    private void setupIngredientComboBox() {
        ingredientComboBox.setItems(availableIngredients);
        ingredientComboBox.setPromptText("Elegir ingrediente...");

        // Custom cell factory to display ingredient names
        ingredientComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Ingredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        ingredientComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Ingredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    /**
     * Setup the ingredients table
     */
    private void setupIngredientsTable() {
        // Set up cell value factories
        ingredientNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIngredient().getName()));

        amountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getAmount())));

        unitColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUnit()));

        notesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNotes()));

        ingredientsTable.setItems(ingredientRows);

        // Add context menu for deleting rows
        ingredientsTable.setRowFactory(tv -> {
            TableRow<RecipeIngredientRow> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.setOnAction(_ -> {
                RecipeIngredientRow item = row.getItem();
                if (item != null) {
                    ingredientRows.remove(item);
                }
            });
            contextMenu.getItems().add(deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    /**
     * Create a new ingredient
     */
    @FXML
    public void createNewIngredient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(UIConstants.INGREDIENT_VIEW_PATH));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            IngredientController ingredientController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Ingrediente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Apply styles
            stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

            stage.showAndWait();

            // Check if ingredient was saved
            if (ingredientController.isSaved()) {
                Ingredient newIngredient = ingredientController.getCreatedIngredient();
                availableIngredients.add(newIngredient);
                // schedule the selection on the next pulse to avoid interfering with internal ListView events
                Platform.runLater(() -> ingredientComboBox.getSelectionModel().select(newIngredient));
                log.info("New ingredient added: {}", newIngredient.getName());
            }

        } catch (IOException e) {
            log.error("Error loading ingredient form", e);
        }
    }

    /**
     * Add the selected ingredient from ComboBox to the table
     */
    @FXML
    public void addSelectedIngredient() {
        Ingredient selectedIngredient = ingredientComboBox.getSelectionModel().getSelectedItem();

        if (selectedIngredient == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Ingrediente");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un ingrediente de la lista.");
            alert.showAndWait();
            return;
        }

        // Show dialog to input amount, unit, and notes
        showIngredientDetailsDialog(selectedIngredient);
    }

    /**
     * Show dialog to input ingredient amount, unit, and notes
     */
    private void showIngredientDetailsDialog(Ingredient ingredient) {
        Dialog<RecipeIngredientRow> dialog = new Dialog<>();
        dialog.setTitle("Detalles del Ingrediente");
        dialog.setHeaderText("Añadir " + ingredient.getName() + " a la receta");

        // Set button types
        ButtonType addButtonType = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create form fields
        TextField amountField = new TextField();
        amountField.setPromptText("Cantidad");
        amountField.getStyleClass().add("form-control");

        TextField unitField = new TextField();
        unitField.setPromptText("Unidad (g, ml, taza, etc.)");
        unitField.getStyleClass().add("form-control");

        TextField notesField = new TextField();
        notesField.setPromptText("Notas (opcional)");
        notesField.getStyleClass().add("form-control");

        // Layout
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Cantidad:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Unidad:"), 0, 1);
        grid.add(unitField, 1, 1);
        grid.add(new Label("Notas:"), 0, 2);
        grid.add(notesField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Style the dialog
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    String unit = unitField.getText().trim();
                    String notes = notesField.getText().trim();

                    if (unit.isEmpty()) {
                        unit = "unidad";
                    }

                    return new RecipeIngredientRow(ingredient, amount, unit, notes);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Por favor, introduce una cantidad válida.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        Optional<RecipeIngredientRow> result = dialog.showAndWait();
        result.ifPresent(row -> {
            ingredientRows.add(row);
            ingredientComboBox.getSelectionModel().clearSelection();
        });
    }

    /**
     * Save the recipe
     */
    @FXML
    public void saveRecipe() {
        // Validate required fields
        if (titleField.getText().trim().isEmpty() ||
            instructionsArea.getText().trim().isEmpty()) {

            log.warn("Required fields are missing");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos Requeridos");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, completa todos los campos obligatorios (Título e Instrucciones).");
            alert.showAndWait();
            return;
        }

        // Create or update recipe object
        Recipe recipe = isEditMode && editingRecipe != null ? editingRecipe : new Recipe();
        recipe.setTitle(titleField.getText().trim());
        recipe.setDescription(descriptionArea.getText().trim());
        recipe.setInstructions(instructionsArea.getText().trim());
        recipe.setNotes(notesArea.getText().trim());

        // Parse time
        try {
            if (!timeField.getText().trim().isEmpty()) {
                recipe.setTimeMinutes(Integer.parseInt(timeField.getText().trim()));
            } else {
                recipe.setTimeMinutes(null);
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid time value");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Tiempo Inválido");
            alert.setHeaderText(null);
            alert.setContentText("El tiempo de preparación debe ser un número entero.");
            alert.showAndWait();
            return;
        }

        // Clear existing ingredients if editing
        if (isEditMode) {
            recipe.getIngredients().clear();
        }

        // Add ingredients
        for (RecipeIngredientRow row : ingredientRows) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.setIngredient(row.getIngredient());
            ri.setAmount(row.getAmount());
            ri.setUnit(row.getUnit());
            ri.setNotes(row.getNotes());
            recipe.addIngredient(ri);
        }

        // Persist to database
        try {
            Recipe savedRecipe = recipeService.save(recipe);
            log.info("Recipe {} successfully: {}", isEditMode ? "updated" : "created", savedRecipe.getTitle());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Receta Guardada");
            alert.setHeaderText(null);
            alert.setContentText("La receta \"" + savedRecipe.getTitle() + "\" se ha guardado correctamente.");
            alert.showAndWait();

            // Go back
            goBack();
        } catch (Exception e) {
            log.error("Error saving recipe: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al Guardar");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo guardar la receta. Detalles: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Cancel recipe creation
     */
    @FXML
    public void cancelRecipe() {
        goBack();
    }

    /**
     * Go back to calendar view
     */
    @FXML
    public void goBack() {
        if (homeController != null) {
            homeController.showCalendarView();
        }
    }

    /**
     * Set recipe for editing mode
     */
    public void setRecipeForEdit(Recipe recipe) {
        if (recipe == null) {
            log.warn("Cannot edit null recipe");
            return;
        }

        this.editingRecipe = recipe;
        this.isEditMode = true;
        loadRecipeData(recipe);
    }

    /**
     * Load recipe data into form fields
     */
    private void loadRecipeData(Recipe recipe) {
        // Load basic fields
        titleField.setText(recipe.getTitle());
        descriptionArea.setText(recipe.getDescription() != null ? recipe.getDescription() : "");
        instructionsArea.setText(recipe.getInstructions());
        notesArea.setText(recipe.getNotes() != null ? recipe.getNotes() : "");
        timeField.setText(recipe.getTimeMinutes() != null ? recipe.getTimeMinutes().toString() : "");

        // Load ingredients
        ingredientRows.clear();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ri : recipe.getIngredients()) {
                RecipeIngredientRow row = new RecipeIngredientRow(
                    ri.getIngredient(),
                    ri.getAmount(),
                    ri.getUnit(),
                    ri.getNotes() != null ? ri.getNotes() : ""
                );
                ingredientRows.add(row);
            }
        }
    }

    /**
     * Inner class to represent a row in the ingredients table
     */
    public static class RecipeIngredientRow {
        private final Ingredient ingredient;
        private final double amount;
        private final String unit;
        private final String notes;

        public RecipeIngredientRow(Ingredient ingredient, double amount, String unit, String notes) {
            this.ingredient = ingredient;
            this.amount = amount;
            this.unit = unit;
            this.notes = notes;
        }

        public Ingredient getIngredient() {
            return ingredient;
        }

        public double getAmount() {
            return amount;
        }

        public String getUnit() {
            return unit;
        }

        public String getNotes() {
            return notes;
        }
    }
}
