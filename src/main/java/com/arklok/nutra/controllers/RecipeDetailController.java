package com.arklok.nutra.controllers;

import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Recipe;
import com.arklok.nutra.models.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecipeDetailController implements IController {

    private static final Logger log = LoggerFactory.getLogger(RecipeDetailController.class);

    @FXML
    private Text titleText;

    @FXML
    private VBox descriptionSection;

    @FXML
    private Text descriptionText;

    @FXML
    private Text timeText;

    @FXML
    private Text ingredientCountText;

    @FXML
    private VBox ingredientsContainer;

    @FXML
    private Text instructionsText;

    @FXML
    private VBox notesSection;

    @FXML
    private Text notesText;

    private HomeController homeController;
    private RecipeListController recipeListController;
    private Recipe currentRecipe;

    public void initialize() {
        log.info("Recipe detail controller initialized");
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setRecipeListController(RecipeListController recipeListController) {
        this.recipeListController = recipeListController;
    }

    /**
     * Set the recipe to display
     */
    public void setRecipe(Recipe recipe) {
        this.currentRecipe = recipe;
        loadRecipeData(recipe);
    }

    /**
     * Load recipe data into the view
     */
    private void loadRecipeData(Recipe recipe) {
        // Set title
        titleText.setText(recipe.getTitle());

        // Set description
        if (recipe.getDescription() != null && !recipe.getDescription().isEmpty()) {
            descriptionText.setText(recipe.getDescription());
            descriptionSection.setVisible(true);
            descriptionSection.setManaged(true);
        } else {
            descriptionSection.setVisible(false);
            descriptionSection.setManaged(false);
        }

        // Set time
        if (recipe.getTimeMinutes() != null) {
            timeText.setText(recipe.getTimeMinutes() + " minutos");
        } else {
            timeText.setText("No especificado");
        }

        // Set ingredient count
        if (recipe.getIngredients() != null) {
            ingredientCountText.setText(recipe.getIngredients().size() + " ingredientes");
            loadIngredients(recipe);
        } else {
            ingredientCountText.setText("0 ingredientes");
        }

        // Set instructions
        instructionsText.setText(recipe.getInstructions());

        // Set notes
        if (recipe.getNotes() != null && !recipe.getNotes().isEmpty()) {
            notesText.setText(recipe.getNotes());
            notesSection.setVisible(true);
            notesSection.setManaged(true);
        } else {
            notesSection.setVisible(false);
            notesSection.setManaged(false);
        }
    }

    /**
     * Load ingredients into the container
     */
    private void loadIngredients(Recipe recipe) {
        ingredientsContainer.getChildren().clear();

        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            Text emptyText = new Text("No hay ingredientes");
            emptyText.setStyle("-fx-fill: " + UIConstants.COLOR_SECONDARY + ";");
            ingredientsContainer.getChildren().add(emptyText);
            return;
        }

        for (RecipeIngredient ri : recipe.getIngredients()) {
            HBox ingredientRow = createIngredientRow(ri);
            ingredientsContainer.getChildren().add(ingredientRow);
        }
    }

    /**
     * Create a row for an ingredient
     */
    private HBox createIngredientRow(RecipeIngredient ri) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-padding: 10px;");

        // Ingredient name
        VBox nameBox = new VBox(3);
        nameBox.setPrefWidth(300);
        Text nameText = new Text(ri.getIngredient().getName());
        nameText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");
        nameBox.getChildren().add(nameText);

        // Add notes if available
        if (ri.getNotes() != null && !ri.getNotes().isEmpty()) {
            Text notesText = new Text(ri.getNotes());
            notesText.setStyle("-fx-font-size: 12px; -fx-fill: " + UIConstants.COLOR_SECONDARY + ";");
            nameBox.getChildren().add(notesText);
        }

        // Amount and unit
        HBox amountBox = new HBox(5);
        amountBox.setAlignment(Pos.CENTER_LEFT);
        amountBox.setPrefWidth(150);

        Text amountText = new Text(String.format("%.1f", ri.getAmount()));
        amountText.setStyle("-fx-font-size: 14px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

        Text unitText = new Text(ri.getUnit());
        unitText.setStyle("-fx-font-size: 14px; -fx-fill: " + UIConstants.COLOR_SECONDARY + ";");

        amountBox.getChildren().addAll(amountText, unitText);

        row.getChildren().addAll(nameBox, amountBox);

        return row;
    }

    /**
     * Edit the current recipe
     */
    @FXML
    public void editRecipe() {
        if (homeController != null && currentRecipe != null) {
            homeController.showEditRecipe(currentRecipe);
        } else {
            log.error("Cannot edit recipe - homeController or currentRecipe is null");
        }
    }

    /**
     * Go back to recipe list
     */
    @FXML
    public void goBack() {
        if (homeController != null) {
            if (recipeListController != null) {
                homeController.showRecipeList();
            } else {
                homeController.showCalendarView();
            }
        } else {
            log.error("HomeController is null! Cannot go back.");
        }
    }
}

