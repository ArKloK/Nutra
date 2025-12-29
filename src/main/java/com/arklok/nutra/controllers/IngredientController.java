package com.arklok.nutra.controllers;

import com.arklok.nutra.enums.Allergen;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Ingredient;
import com.arklok.nutra.services.IngredientService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static javafx.geometry.Pos.CENTER_LEFT;

@Component
public class IngredientController implements IController {

    private static final Logger log = LoggerFactory.getLogger(IngredientController.class);

    private final IngredientService ingredientService;

    @FXML
    private TextField nameField;

    @FXML
    private TextField caloriesField;

    @FXML
    private TextField carbohydratesField;

    @FXML
    private TextField sugarField;

    @FXML
    private TextField proteinField;

    @FXML
    private TextField fiberField;

    @FXML
    private TextField fatField;

    @FXML
    private TextField saturatedFatField;

    @FXML
    private TextField unsaturatedFatField;

    @FXML
    private VBox vitaminsContainer;

    @FXML
    private VBox mineralsContainer;

    @FXML
    private ComboBox<Allergen> allergenComboBox;

    @FXML
    private VBox allergensContainer;

    private Ingredient createdIngredient;
    private Ingredient editingIngredient;
    private boolean saved = false;
    private boolean isEditMode = false;

    private final List<VitaminMineralEntry> vitaminEntries = new ArrayList<>();
    private final List<VitaminMineralEntry> mineralEntries = new ArrayList<>();
    private final List<Allergen> selectedAllergens = new ArrayList<>();
    private HomeController homeController;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    public void initialize() {
        log.info("Ingredient controller initialized");
        loadAllergens();
        setupAllergenComboBox();
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Load allergens from enum into ComboBox
     */
    private void loadAllergens() {
        if (allergenComboBox == null) return;
        allergenComboBox.setItems(FXCollections.observableArrayList(Allergen.values()));
        // ensure no initial value
        Platform.runLater(() -> allergenComboBox.setValue(null));
    }

    /**
     * Setup allergen ComboBox listener to add allergen when selected
     */
    private void setupAllergenComboBox() {
        if (allergenComboBox == null) return;

        // Ensure the prompt text is shown when no value is set by providing a custom button cell
        final String prompt = "+ Añadir Alérgeno";
        allergenComboBox.setPromptText(prompt);

        allergenComboBox.setButtonCell(new ListCell<Allergen>() {
            @Override
            protected void updateItem(Allergen item, boolean empty) {
                super.updateItem(item, empty);
                // Always show the prompt in the button area so the prompt doesn't disappear
                setText(prompt);
            }
        });

        allergenComboBox.setCellFactory(listView -> new ListCell<Allergen>() {
            @Override
            protected void updateItem(Allergen item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });

        // Ensure prompt is displayed even when value is null by adding a converter
        allergenComboBox.setConverter(new StringConverter<Allergen>() {
            @Override
            public String toString(Allergen object) {
                // Always return the prompt for the button area so it never shows the selected value
                return prompt;
            }

            @Override
            public Allergen fromString(String string) {
                return null; // not used
            }
        });

        // Handle selection but defer actual mutation to the next pulse to avoid JavaFX internal reentrancy
        allergenComboBox.setOnAction((ActionEvent event) -> {
            // capture the selected item now
            final Allergen selected = allergenComboBox.getSelectionModel() != null
                    ? allergenComboBox.getSelectionModel().getSelectedItem()
                    : allergenComboBox.getValue();

            // consume the event now to avoid further processing here
            event.consume();

            if (selected == null) return;

            // Defer the mutation (add and clear) to the next pulse to avoid internal ListView selection conflicts
            Platform.runLater(() -> {
                try {
                    if (!selectedAllergens.contains(selected)) {
                        addAllergenToList(selected);
                    }
                } catch (Exception ignored) {
                } finally {
                    try { if (allergenComboBox.getSelectionModel() != null) allergenComboBox.getSelectionModel().clearSelection(); } catch (Exception ignore) {}
                    try { allergenComboBox.setValue(null); } catch (Exception ignore) {}
                    try { allergenComboBox.hide(); } catch (Exception ignore) {}
                }
            });
        });
    }

    /**
     * Add an allergen to the visual list
     */
    private void addAllergenToList(Allergen allergen) {
        if (allergen == null) return;
        if (selectedAllergens.contains(allergen)) return; // guard duplicate

        selectedAllergens.add(allergen);

        HBox allergenRow = new HBox(10);
        allergenRow.setPadding(new Insets(5, 0, 5, 0));
        allergenRow.setAlignment(CENTER_LEFT);

        TextField allergenField = new TextField(allergen.toString());
        allergenField.setEditable(false);
        allergenField.getStyleClass().add("form-control-readonly");
        allergenField.setPrefWidth(250);

        Button removeButton = new Button("✕");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction((ActionEvent e) -> {
            e.consume();

            // actual removal via shared helper to preserve focus behavior
            removeRowWithFocusPreservation(allergenRow, () -> {
                if (allergensContainer != null) {
                    allergensContainer.getChildren().remove(allergenRow);
                }
                selectedAllergens.remove(allergen);
            });
        });

        allergenRow.getChildren().addAll(allergenField, removeButton);
        if (allergensContainer != null) {
            allergensContainer.getChildren().add(allergenRow);
        }
    }

    /**
     * Remove a row from the scene preserving/restoring focus in a safe way.
     * The provided onRemove runnable will be executed to actually remove model/children.
     */
    private void removeRowWithFocusPreservation(HBox row, Runnable onRemove) {
        if (row == null || onRemove == null) return;

        // capture current focus owner
        Node focusOwner = null;
        if (row.getScene() != null) {
            focusOwner = row.getScene().getFocusOwner();
        } else if (allergensContainer != null && allergensContainer.getScene() != null) {
            focusOwner = allergensContainer.getScene().getFocusOwner();
        }

        // perform removal
        try {
            onRemove.run();
        } catch (Exception ex) {
            // swallow to avoid UI interruption
            log.warn("Error while removing row: {}", ex.getMessage());
        }

        final Node previousFocus = focusOwner;
        // restore focus on next pulse
        Platform.runLater(() -> {
            try {
                if (previousFocus != null && previousFocus.getScene() != null && !isDescendant(previousFocus, row)) {
                    previousFocus.requestFocus();
                } else {
                    if (allergenComboBox != null) allergenComboBox.requestFocus();
                }
            } catch (Exception ex) {
                if (allergenComboBox != null) allergenComboBox.requestFocus();
            }
        });
    }

    /**
     * Returns true if node is a descendant of ancestor (walks up parent chain)
     */
    private boolean isDescendant(Node node, Node ancestor) {
        if (node == null || ancestor == null) return false;
        Parent parent = node.getParent();
        while (parent != null) {
            if (parent == ancestor) return true;
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Add a new vitamin entry field
     */
    @FXML
    public void addVitamin() {
        VitaminMineralEntry entry = createVitaminMineralEntry(vitaminsContainer, vitaminEntries);
        vitaminEntries.add(entry);
    }

    /**
     * Add a new mineral entry field
     */
    @FXML
    public void addMineral() {
        VitaminMineralEntry entry = createVitaminMineralEntry(mineralsContainer, mineralEntries);
        mineralEntries.add(entry);
    }

    /**
     * Create a vitamin/mineral entry row
     */
    private VitaminMineralEntry createVitaminMineralEntry(VBox container, List<VitaminMineralEntry> list) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(5, 0, 5, 0));

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre (ej: Vitamina C, Hierro)");
        nameField.getStyleClass().add("form-control");
        nameField.setPrefWidth(250);

        TextField valueField = new TextField();
        valueField.setPromptText("Valor (mg)");
        valueField.getStyleClass().add("form-control");
        valueField.setPrefWidth(120);

        Button removeButton = new Button("✕");
        removeButton.getStyleClass().add("remove-button");

        // create entry first so we can reference it in the remove handler
        VitaminMineralEntry entry = new VitaminMineralEntry(row, nameField, valueField);

        removeButton.setOnAction((javafx.event.ActionEvent e) -> {
            e.consume();
            removeRowWithFocusPreservation(row, () -> {
                if (container != null) container.getChildren().remove(row);
                list.remove(entry);
            });
        });

        row.getChildren().addAll(nameField, valueField, removeButton);
        if (container != null) container.getChildren().add(row);

        return entry;
    }

    /**
     * Save the ingredient
     */
    @FXML
    public void saveIngredient() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            log.warn("Name field is required");
            // TODO: Show error message to user
            return;
        }

        // Create or update ingredient object
        Ingredient ingredient = isEditMode && editingIngredient != null ? editingIngredient : new Ingredient();
        ingredient.setName(nameField.getText().trim());

        // Set numeric fields (with null safety)
        ingredient.setCalories(parseDouble(caloriesField.getText()));
        ingredient.setCarbohydrates(parseDouble(carbohydratesField.getText()));
        ingredient.setSugar(parseDouble(sugarField.getText()));
        ingredient.setProtein(parseDouble(proteinField.getText()));
        ingredient.setFiber(parseDouble(fiberField.getText()));
        ingredient.setFat(parseDouble(fatField.getText()));
        ingredient.setSaturatedFat(parseDouble(saturatedFatField.getText()));
        ingredient.setUnsaturatedFat(parseDouble(unsaturatedFatField.getText()));

        // Set vitamins
        Map<String, Float> vitamins = new HashMap<>();
        for (VitaminMineralEntry entry : vitaminEntries) {
            String name = entry.nameField.getText().trim();
            String value = entry.valueField.getText().trim();
            if (!name.isEmpty() && !value.isEmpty()) {
                try {
                    vitamins.put(name, Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    log.warn("Invalid vitamin value: {}", value);
                }
            }
        }
        ingredient.setVitamins(vitamins);

        // Set minerals
        Map<String, Float> minerals = new HashMap<>();
        for (VitaminMineralEntry entry : mineralEntries) {
            String name = entry.nameField.getText().trim();
            String value = entry.valueField.getText().trim();
            if (!name.isEmpty() && !value.isEmpty()) {
                try {
                    minerals.put(name, Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    log.warn("Invalid mineral value: {}", value);
                }
            }
        }
        ingredient.setMinerals(minerals);

        // Set allergens
        Set<Allergen> allergens = new HashSet<>(selectedAllergens);
        ingredient.setAllergens(allergens);

        // Persist to database
        try {
            createdIngredient = ingredientService.save(ingredient);
            log.info("Ingredient {} successfully: {}", isEditMode ? "updated" : "created", createdIngredient.getName());
            saved = true;
            goBack();
        } catch (Exception e) {
            log.error("Error saving ingredient: {}", e.getMessage(), e);
            showError("Error al guardar ingrediente",
                     "No se pudo guardar el ingrediente. Detalles: " + e.getMessage());
        }
    }

    /**
     * Cancel ingredient creation
     */
    @FXML
    public void cancelIngredient() {
        saved = false;
        createdIngredient = null;
        goBack();
    }

    /**
     * Close the ingredient window
     */
    private void goBack() {
        if (homeController != null) {
            homeController.showPatientList();
        }
    }

    /**
     * Parse double value safely
     */
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Check if ingredient was saved
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Get the created ingredient
     */
    public Ingredient getCreatedIngredient() {
        return createdIngredient;
    }

    /**
     * Set ingredient for editing mode
     */
    public void setIngredientForEdit(Ingredient ingredient) {
        if (ingredient == null) {
            log.warn("Cannot edit null ingredient");
            return;
        }

        this.editingIngredient = ingredient;
        this.isEditMode = true;
        loadIngredientData(ingredient);
    }

    /**
     * Load ingredient data into form fields
     */
    private void loadIngredientData(Ingredient ingredient) {
        // Load basic fields
        nameField.setText(ingredient.getName());
        caloriesField.setText(ingredient.getCalories() != null ? ingredient.getCalories().toString() : "");
        carbohydratesField.setText(ingredient.getCarbohydrates() != null ? ingredient.getCarbohydrates().toString() : "");
        sugarField.setText(ingredient.getSugar() != null ? ingredient.getSugar().toString() : "");
        proteinField.setText(ingredient.getProtein() != null ? ingredient.getProtein().toString() : "");
        fiberField.setText(ingredient.getFiber() != null ? ingredient.getFiber().toString() : "");
        fatField.setText(ingredient.getFat() != null ? ingredient.getFat().toString() : "");
        saturatedFatField.setText(ingredient.getSaturatedFat() != null ? ingredient.getSaturatedFat().toString() : "");
        unsaturatedFatField.setText(ingredient.getUnsaturatedFat() != null ? ingredient.getUnsaturatedFat().toString() : "");

        // Load vitamins
        if (ingredient.getVitamins() != null) {
            for (Map.Entry<String, Float> entry : ingredient.getVitamins().entrySet()) {
                VitaminMineralEntry vitaminEntry = createVitaminMineralEntry(vitaminsContainer, vitaminEntries);
                vitaminEntry.nameField.setText(entry.getKey());
                vitaminEntry.valueField.setText(entry.getValue().toString());
                vitaminEntries.add(vitaminEntry);
            }
        }

        // Load minerals
        if (ingredient.getMinerals() != null) {
            for (Map.Entry<String, Float> entry : ingredient.getMinerals().entrySet()) {
                VitaminMineralEntry mineralEntry = createVitaminMineralEntry(mineralsContainer, mineralEntries);
                mineralEntry.nameField.setText(entry.getKey());
                mineralEntry.valueField.setText(entry.getValue().toString());
                mineralEntries.add(mineralEntry);
            }
        }

        // Load allergens
        if (ingredient.getAllergens() != null) {
            for (Allergen allergen : ingredient.getAllergens()) {
                addAllergenToList(allergen);
            }
        }
    }

    /**
     * Delete an ingredient by id
     */
    public void deleteIngredient(Long ingredientId) {
        if (ingredientId == null) {
            log.warn("Cannot delete ingredient with null id");
            return;
        }

        try {
            ingredientService.deleteById(ingredientId);
            log.info("Ingredient with id {} successfully deleted", ingredientId);
        } catch (Exception e) {
            log.error("Error deleting ingredient: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting ingredient", e);
        }
    }

    /**
     * Get all ingredients from database
     */
    public List<Ingredient> getAllIngredients() {
        return ingredientService.findAll();
    }

    /**
     * Search ingredients by name
     */
    public List<Ingredient> searchIngredients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllIngredients();
        }
        return ingredientService.searchByName(searchTerm.trim());
    }

    /**
     * Find ingredient by id with all details loaded
     */
    public Ingredient findIngredientById(Long id) {
        return ingredientService.findByIdWithDetails(id).orElse(null);
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
        * Inner class to hold vitamin/mineral entry components
    */
    private record VitaminMineralEntry(HBox row, TextField nameField, TextField valueField) { }
}
