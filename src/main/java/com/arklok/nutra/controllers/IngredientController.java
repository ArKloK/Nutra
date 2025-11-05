package com.arklok.nutra.controllers;

import com.arklok.nutra.models.Ingredient;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IngredientController{

    private static final Logger log = LoggerFactory.getLogger(IngredientController.class);

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

    private Ingredient createdIngredient;
    private boolean saved = false;

    private final List<VitaminMineralEntry> vitaminEntries = new ArrayList<>();
    private final List<VitaminMineralEntry> mineralEntries = new ArrayList<>();

    public void initialize() {
        log.info("Ingredient controller initialized");
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
        removeButton.setOnAction(e -> {
            container.getChildren().remove(row);
            list.remove(new VitaminMineralEntry(row, nameField, valueField));
        });

        row.getChildren().addAll(nameField, valueField, removeButton);
        container.getChildren().add(row);

        return new VitaminMineralEntry(row, nameField, valueField);
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

        // Create ingredient object
        createdIngredient = new Ingredient();
        createdIngredient.setName(nameField.getText().trim());

        // Set numeric fields (with null safety)
        createdIngredient.setCalories(parseDouble(caloriesField.getText()));
        createdIngredient.setCarbohydrates(parseDouble(carbohydratesField.getText()));
        createdIngredient.setSugar(parseDouble(sugarField.getText()));
        createdIngredient.setProtein(parseDouble(proteinField.getText()));
        createdIngredient.setFiber(parseDouble(fiberField.getText()));
        createdIngredient.setFat(parseDouble(fatField.getText()));
        createdIngredient.setSaturatedFat(parseDouble(saturatedFatField.getText()));
        createdIngredient.setUnsaturatedFat(parseDouble(unsaturatedFatField.getText()));

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
        createdIngredient.setVitamins(vitamins);

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
        createdIngredient.setMinerals(minerals);

        log.info("Ingredient created: {}", createdIngredient.getName());
        saved = true;
        closeWindow();
    }

    /**
     * Cancel ingredient creation
     */
    @FXML
    public void cancelIngredient() {
        saved = false;
        createdIngredient = null;
        closeWindow();
    }

    /**
     * Close the ingredient window
     */
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
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
     * Inner class to hold vitamin/mineral entry components
     */
    private static class VitaminMineralEntry {
        final HBox row;
        final TextField nameField;
        final TextField valueField;

        VitaminMineralEntry(HBox row, TextField nameField, TextField valueField) {
            this.row = row;
            this.nameField = nameField;
            this.valueField = valueField;
        }
    }
}

