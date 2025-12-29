package com.arklok.nutra.controllers;

import com.arklok.nutra.helpers.UIHelper;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Consultation;
import com.arklok.nutra.services.ConsultationService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HomeController {
    @FXML
    public Text monday_text;
    @FXML
    public Text tuesday_text;
    @FXML
    public Text wednesday_text;
    @FXML
    public Text thursday_text;
    @FXML
    public Text friday_text;
    @FXML
    public Text saturday_text;
    @FXML
    public Text sunday_text;
    @FXML
    public Text monthYearText;
    @FXML
    public Button previousWeekButton;
    @FXML
    public Button nextWeekButton;
    @FXML
    public Button currentWeekButton;
    @FXML
    public GridPane calendarGrid;
    @FXML
    public StackPane contentPane;
    @FXML
    public VBox calendarView;
    @FXML
    public VBox consultationView;
    @FXML
    public VBox patientView;
    @FXML
    public VBox patientListView;
    @FXML
    public VBox patientDetailView;
    @FXML
    public VBox recipeView;
    @FXML
    public VBox recipeListView;
    @FXML
    public VBox recipeDetailView;
    @FXML
    public VBox ingredientListView;
    @FXML
    public ScrollPane mondayContainer;
    @FXML
    public ScrollPane tuesdayContainer;
    @FXML
    public ScrollPane wednesdayContainer;
    @FXML
    public ScrollPane thursdayContainer;
    @FXML
    public ScrollPane fridayContainer;
    @FXML
    public ScrollPane saturdayContainer;
    @FXML
    public ScrollPane sundayContainer;
    @FXML
    public VBox mondayContent;
    @FXML
    public VBox tuesdayContent;
    @FXML
    public VBox wednesdayContent;
    @FXML
    public VBox thursdayContent;
    @FXML
    public VBox fridayContent;
    @FXML
    public VBox saturdayContent;
    @FXML
    public VBox sundayContent;

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ApplicationContext applicationContext;
    private final ConsultationService consultationService;

    private LocalDate currentWeekStart;
    private LocalDate selectedDateForNewConsultation;
    private Map<LocalDate, VBox> dayContainers;
    private ContextMenu currentContextMenu;

    public HomeController(ApplicationContext applicationContext, ConsultationService consultationService) {
        this.applicationContext = applicationContext;
        this.consultationService = consultationService;
    }

    public void initialize() {
        UIHelper.InitializeUI();
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Initialize day containers map
        initializeDayContainers();

        // Setup context menus for each day
        setupContextMenus();

        // Update week days and load consultations
        updateWeekDays();
        loadConsultationsForWeek();
    }

    // PUBLIC METHODS
    // -------------------------------------------------------------------------------

    /**
     * Navigate to the previous week with transition animation
     */
    @FXML
    public void goToPreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        animateWeekTransition(true);
        loadConsultationsForWeek();
    }

    /**
     * Navigate to the next week with transition animation
     */
    @FXML
    public void goToNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        animateWeekTransition(false);
        loadConsultationsForWeek();
    }

    /**
     * Navigate back to the current week with transition animation
     */
    @FXML
    public void goToCurrentWeek() {
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        animateWeekTransition(false);
        loadConsultationsForWeek();
    }

    /**
     * Show the new consultation form
     */
    @FXML
    public void showNewConsultation() {
        showNewConsultation(null);
    }

    /**
     * Show the new consultation form with a preselected date
     */
    public void showNewConsultation(LocalDate date) {
        try {
            // Reload consultation view to get a fresh form
            consultationView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/consultation.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox consultationContent = loader.load();

            // Get the controller and set reference to this controller
            ConsultationController controller = loader.getController();
            controller.setHomeController(this);

            // Set the preselected date if provided
            if (date != null) {
                controller.setPreselectedDate(date);
            }

            // Add content to view
            consultationView.getChildren().add(consultationContent);

            // Switch views with fade transition
            switchToView(consultationView);
        } catch (IOException e) {
            log.error("Error loading consultation view", e);
        }
    }

    /**
     * Show the new patient form
     */
    @FXML
    public void showNewPatient() {
        try {
            // Reload patient view to get a fresh form
            patientView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox patientContent = loader.load();

            // Get the controller and set reference to this controller
            PatientController controller = loader.getController();
            controller.setHomeController(this);

            // Add content to view
            patientView.getChildren().add(patientContent);

            // Switch views with fade transition
            switchToView(patientView);
        } catch (IOException e) {
            log.error("Error loading patient view", e);
        }
    }

    /**
     * Show the edit patient form
     */
    public void showEditPatient(com.arklok.nutra.models.Patient patient, PatientDetailController patientDetailController) {
        try {
            log.info("Loading edit patient form for: {} {}", patient.getFirstName(), patient.getLastName());
            // Reload patient view to get a fresh form
            patientView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox patientContent = loader.load();

            // Get the controller and set reference to this controller
            PatientController controller = loader.getController();
            controller.setHomeController(this);
            controller.setPatient(patient); // Set patient for edit mode

            // Add content to view
            patientView.getChildren().add(patientContent);

            // Switch views with fade transition
            log.info("Switching to edit patient view...");
            switchToView(patientView);
        } catch (IOException e) {
            log.error("Error loading edit patient view", e);
        }
    }

    /**
     * Show the patient list
     */
    @FXML
    public void showPatientList() {
        try {
            log.info("Loading patient list view...");
            // Reload patient list view to refresh data
            patientListView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient_list.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox patientListContent = loader.load();

            // Get the controller and set reference to this controller
            PatientListController controller = loader.getController();
            controller.setHomeController(this);

            // Add content to view
            patientListView.getChildren().add(patientListContent);

            // Switch views with fade transition
            log.info("Switching to patient list view...");
            switchToView(patientListView);
        } catch (IOException e) {
            log.error("Error loading patient list view", e);
        }
    }

    /**
     * Show the ingredient list view
     */
    @FXML
    public void showIngredientList() {
        try {
            log.info("Loading ingredient list view...");
            // Reload ingredient list view to refresh data
            ingredientListView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ingredient_list.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox ingredientListContent = loader.load();

            // Get the controller and set reference to this controller
            IngredientListController controller = loader.getController();
            controller.setHomeController(this);

            // Add content to view
            ingredientListView.getChildren().add(ingredientListContent);

            // Switch views with fade transition
            log.info("Switching to ingredient list view...");
            switchToView(ingredientListView);
        } catch (IOException e) {
            log.error("Error loading ingredient list view", e);
        }
    }

    /**
     * Show the patient detail view
     */
    public void showPatientDetail(com.arklok.nutra.models.Patient patient, PatientListController patientListController) {
        try {
            log.info("Loading patient detail view for patient: {} {}", patient.getFirstName(), patient.getLastName());
            // Reload patient detail view
            patientDetailView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient_detail.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox patientDetailContent = loader.load();

            // Get the controller and set reference to this controller
            PatientDetailController controller = loader.getController();
            controller.setHomeController(this);
            controller.setPatientListController(patientListController);
            controller.setPatient(patient);

            // Add content to view
            patientDetailView.getChildren().add(patientDetailContent);

            // Switch views with fade transition
            log.info("Switching to patient detail view...");
            switchToView(patientDetailView);
        } catch (IOException e) {
            log.error("Error loading patient detail view", e);
        }
    }

    /**
     * Show the new recipe form
     */
    @FXML
    public void showNewRecipe() {
        try {
            // Load recipe view if not already loaded
            recipeView.getChildren().clear();
            AddContentToView("/fxml/recipe.fxml", recipeView);

            // Switch views with fade transition
            switchToView(recipeView);
        } catch (IOException e) {
            log.error("Error loading recipe view", e);
        }
    }

    /**
     * Show the recipe list view
     */
    @FXML
    public void showRecipeList() {
        try {
            log.info("Loading recipe list view...");
            // Reload recipe list view to refresh data
            recipeListView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recipe_list.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox recipeListContent = loader.load();

            // Get the controller and set reference to this controller
            RecipeListController controller = loader.getController();
            controller.setHomeController(this);

            // Add content to view
            recipeListView.getChildren().add(recipeListContent);

            // Switch views with fade transition
            log.info("Switching to recipe list view...");
            switchToView(recipeListView);
        } catch (IOException e) {
            log.error("Error loading recipe list view", e);
        }
    }

    /**
     * Show the recipe edit form
     */
    public void showEditRecipe(com.arklok.nutra.models.Recipe recipe) {
        try {
            log.info("Loading recipe edit view for recipe: {}", recipe.getTitle());
            // Reload recipe view
            recipeView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recipe.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox recipeContent = loader.load();

            // Get the controller and set the recipe for editing
            RecipeController controller = loader.getController();
            controller.setHomeController(this);
            controller.setRecipeForEdit(recipe);

            // Add content to view
            recipeView.getChildren().add(recipeContent);

            // Switch views with fade transition
            log.info("Switching to recipe edit view...");
            switchToView(recipeView);
        } catch (IOException e) {
            log.error("Error loading recipe edit view", e);
        }
    }

    /**
     * Show the recipe detail view
     */
    public void showRecipeDetail(com.arklok.nutra.models.Recipe recipe, RecipeListController recipeListController) {
        try {
            log.info("Loading recipe detail view for recipe: {}", recipe.getTitle());
            // Reload recipe detail view
            recipeDetailView.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recipe_detail.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            VBox recipeDetailContent = loader.load();

            // Get the controller and set the recipe
            RecipeDetailController controller = loader.getController();
            controller.setHomeController(this);
            controller.setRecipeListController(recipeListController);
            controller.setRecipe(recipe);

            // Add content to view
            recipeDetailView.getChildren().add(recipeDetailContent);

            // Switch views with fade transition
            log.info("Switching to recipe detail view...");
            switchToView(recipeDetailView);
        } catch (IOException e) {
            log.error("Error loading recipe detail view", e);
        }
    }

    /**
     * Show the calendar view
     */
    public void showCalendarView() {
        switchToView(calendarView);
    }

    // PRIVATE METHODS
    // -------------------------------------------------------------------------------

    /**
     * Updates the day numbers and month/year text based on the current week
     */
    private void updateWeekDays() {
        // Update each day text with the corresponding day of week
        monday_text.setText(String.valueOf(currentWeekStart.getDayOfMonth()));
        tuesday_text.setText(String.valueOf(currentWeekStart.plusDays(1).getDayOfMonth()));
        wednesday_text.setText(String.valueOf(currentWeekStart.plusDays(2).getDayOfMonth()));
        thursday_text.setText(String.valueOf(currentWeekStart.plusDays(3).getDayOfMonth()));
        friday_text.setText(String.valueOf(currentWeekStart.plusDays(4).getDayOfMonth()));
        saturday_text.setText(String.valueOf(currentWeekStart.plusDays(5).getDayOfMonth()));
        sunday_text.setText(String.valueOf(currentWeekStart.plusDays(6).getDayOfMonth()));

        // Update month and year text (use the middle of the week to get the most representative month)
        LocalDate midWeek = currentWeekStart.plusDays(3);
        String monthName = midWeek.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        monthYearText.setText("- " + monthName + " " + midWeek.getYear());
    }

    /**
     * Animate the transition when changing weeks
     *
     * @param isGoingBack true if going to previous week, false if going to next week
     */
    private void animateWeekTransition(boolean isGoingBack) {
        // Fade out transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), calendarGrid);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        // Slide transition
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(150), calendarGrid);
        slideOut.setFromX(0);
        slideOut.setToX(isGoingBack ? 30 : -30);

        // Combine fade and slide out
        ParallelTransition transitionOut = new ParallelTransition(fadeOut, slideOut);

        transitionOut.setOnFinished(_ -> {
            // Update the week days in the middle of the animation
            updateWeekDays();

            // Fade in transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), calendarGrid);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);

            // Slide in transition
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(150), calendarGrid);
            slideIn.setFromX(isGoingBack ? -30 : 30);
            slideIn.setToX(0);

            // Combine fade and slide in
            ParallelTransition transitionIn = new ParallelTransition(fadeIn, slideIn);
            transitionIn.play();
        });

        transitionOut.play();
    }

    /**
     * Switch between views with a fade transition
     */
    private void switchToView(VBox targetView) {
        // Find current visible view
        VBox currentView = null;
        if (calendarView.isVisible()) {
            currentView = calendarView;
        } else if (consultationView.isVisible()) {
            currentView = consultationView;
        } else if (patientView.isVisible()) {
            currentView = patientView;
        } else if (patientListView.isVisible()) {
            currentView = patientListView;
        } else if (patientDetailView.isVisible()) {
            currentView = patientDetailView;
        } else if (recipeView.isVisible()) {
            currentView = recipeView;
        } else if (recipeListView.isVisible()) {
            currentView = recipeListView;
        } else if (recipeDetailView.isVisible()) {
            currentView = recipeDetailView;
        } else if (ingredientListView.isVisible()) {
            currentView = ingredientListView;
        }

        if (currentView == targetView || currentView == null) {
            return;
        }

        // Fade out current view
        FadeTransition fadeOut = getFadeTransition(targetView, currentView);

        fadeOut.play();
    }

    private void AddContentToView(String fxmlPath, VBox view) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(applicationContext::getBean);
        VBox patientContent = loader.load();

        // Get the controller and set reference to this controller
        IController controller = loader.getController();
        controller.setHomeController(this);

        // Add content to view
        view.getChildren().add(patientContent);
    }

    @NonNull
    private static FadeTransition getFadeTransition(VBox targetView, VBox currentView) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), currentView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(_ -> {
            currentView.setVisible(false);
            currentView.setManaged(false);

            // Show and fade in target view
            targetView.setVisible(true);
            targetView.setManaged(true);
            targetView.setOpacity(0.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), targetView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        return fadeOut;
    }

    /**
     * Initialize the map of day containers
     */
    private void initializeDayContainers() {
        dayContainers = new LinkedHashMap<>();
        dayContainers.put(currentWeekStart, mondayContent);
        dayContainers.put(currentWeekStart.plusDays(1), tuesdayContent);
        dayContainers.put(currentWeekStart.plusDays(2), wednesdayContent);
        dayContainers.put(currentWeekStart.plusDays(3), thursdayContent);
        dayContainers.put(currentWeekStart.plusDays(4), fridayContent);
        dayContainers.put(currentWeekStart.plusDays(5), saturdayContent);
        dayContainers.put(currentWeekStart.plusDays(6), sundayContent);
    }

    /**
     * Update the day containers map when week changes
     */
    private void updateDayContainersMap() {
        dayContainers.clear();
        dayContainers.put(currentWeekStart, mondayContent);
        dayContainers.put(currentWeekStart.plusDays(1), tuesdayContent);
        dayContainers.put(currentWeekStart.plusDays(2), wednesdayContent);
        dayContainers.put(currentWeekStart.plusDays(3), thursdayContent);
        dayContainers.put(currentWeekStart.plusDays(4), fridayContent);
        dayContainers.put(currentWeekStart.plusDays(5), saturdayContent);
        dayContainers.put(currentWeekStart.plusDays(6), sundayContent);
    }

    /**
     * Setup context menus for each day container
     */
    private void setupContextMenus() {
        setupContextMenuForDay(mondayContainer, 0);
        setupContextMenuForDay(tuesdayContainer, 1);
        setupContextMenuForDay(wednesdayContainer, 2);
        setupContextMenuForDay(thursdayContainer, 3);
        setupContextMenuForDay(fridayContainer, 4);
        setupContextMenuForDay(saturdayContainer, 5);
        setupContextMenuForDay(sundayContainer, 6);
    }

    /**
     * Setup context menu for a specific day container
     */
    private void setupContextMenuForDay(ScrollPane dayContainer, int dayOffset) {
        dayContainer.setOnContextMenuRequested(event -> {
            // Close any existing context menu
            if (currentContextMenu != null && currentContextMenu.isShowing()) {
                currentContextMenu.hide();
            }

            LocalDate clickedDate = currentWeekStart.plusDays(dayOffset);
            showContextMenuForDay(dayContainer, event.getScreenX(), event.getScreenY(), clickedDate);
            event.consume();
        });
    }

    /**
     * Show context menu for a specific day
     */
    private void showContextMenuForDay(ScrollPane dayContainer, double screenX, double screenY, LocalDate date) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("context-menu-calendar");

        MenuItem addConsultationItem = new MenuItem("Nueva Consulta");
        addConsultationItem.getStyleClass().add("menu-item-new-consultation");
        addConsultationItem.setOnAction(_ -> {
            selectedDateForNewConsultation = date;
            showNewConsultation(date);
        });

        contextMenu.getItems().add(addConsultationItem);

        // Add listener to clear reference when menu is hidden
        contextMenu.setOnHidden(_ -> {
            if (currentContextMenu == contextMenu) {
                currentContextMenu = null;
            }
        });

        currentContextMenu = contextMenu;
        contextMenu.show(dayContainer, screenX, screenY);
    }

    /**
     * Load consultations for the current week
     */
    private void loadConsultationsForWeek() {
        // Update day containers map
        updateDayContainersMap();

        // Clear all day containers
        clearAllDayContainers();

        // Load consultations from database
        List<Consultation> consultations = consultationService.findByWeek(currentWeekStart);

        // Add consultation cards to their respective days
        for (Consultation consultation : consultations) {
            LocalDate consultationDate = consultation.getDateTime().toLocalDate();
            VBox dayContainer = dayContainers.get(consultationDate);

            if (dayContainer != null) {
                VBox consultationCard = createConsultationCard(consultation);
                dayContainer.getChildren().add(consultationCard);
            }
        }
    }

    /**
     * Clear all day containers
     */
    private void clearAllDayContainers() {
        mondayContent.getChildren().clear();
        tuesdayContent.getChildren().clear();
        wednesdayContent.getChildren().clear();
        thursdayContent.getChildren().clear();
        fridayContent.getChildren().clear();
        saturdayContent.getChildren().clear();
        sundayContent.getChildren().clear();
    }

    /**
     * Create a consultation card for display in the calendar
     */
    private VBox createConsultationCard(Consultation consultation) {
        VBox card = new VBox(5);
        card.getStyleClass().add("consultation-card");
        card.setPadding(new Insets(10));


        // Time label
        Text timeText = new Text(consultation.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeText.getStyleClass().add("consultation-time");

        // Patient name label
        Text patientText = new Text(consultation.getPatientName());
        patientText.getStyleClass().add("consultation-patient");
        patientText.setWrappingWidth(0); // Will be bound later

        // Reason label
        Text reasonText = new Text(consultation.getReason());
        reasonText.getStyleClass().add("consultation-reason");
        reasonText.setWrappingWidth(0); // Will be bound later

        card.getChildren().addAll(timeText, patientText, reasonText);

        // Add click handler to view/edit consultation
        card.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                viewConsultation(consultation);
                event.consume();
            }
        });

        // Add context menu for delete option
        card.setOnContextMenuRequested(event -> {
            // Close any existing context menu
            if (currentContextMenu != null && currentContextMenu.isShowing()) {
                currentContextMenu.hide();
            }

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getStyleClass().add("context-menu-calendar");

            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.getStyleClass().add("menu-item-delete");
            deleteItem.setOnAction(_ -> deleteConsultation(consultation));
            contextMenu.getItems().add(deleteItem);

            // Add listener to clear reference when menu is hidden
            contextMenu.setOnHidden(_ -> {
                if (currentContextMenu == contextMenu) {
                    currentContextMenu = null;
                }
            });

            currentContextMenu = contextMenu;
            contextMenu.show(card, event.getScreenX(), event.getScreenY());

            // Consume the event to prevent it from propagating to parent
            event.consume();
        });

        return card;
    }

    /**
     * View/edit a consultation
     */
    private void viewConsultation(Consultation consultation) {
        // TODO: Implement view/edit consultation
        log.info("Viewing consultation: {}", consultation.getId());
    }

    /**
     * Delete a consultation
     */
    private void deleteConsultation(Consultation consultation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar consulta?");
        alert.setContentText("Esta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                consultationService.delete(consultation);
                loadConsultationsForWeek();
            }
        });
    }

    /**
     * Reload consultations (to be called after saving a new consultation)
     */
    public void reloadConsultations() {
        loadConsultationsForWeek();
    }
}
