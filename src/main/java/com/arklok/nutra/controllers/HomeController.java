package com.arklok.nutra.controllers;

import com.arklok.nutra.helpers.UIHelper;
import com.arklok.nutra.interfaces.IController;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

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
    public VBox recipeView;

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ApplicationContext applicationContext;

    private LocalDate currentWeekStart;

    public HomeController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void initialize() {
        UIHelper.InitializeUI();
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekDays();
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
    }

    /**
     * Navigate to the next week with transition animation
     */
    @FXML
    public void goToNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        animateWeekTransition(false);
    }

    /**
     * Show the new consultation form
     */
    @FXML
    public void showNewConsultation() {
        try {
            // Load consultation view if not already loaded
            if (consultationView.getChildren().isEmpty()) {
                AddContentToView("/fxml/consultation.fxml", consultationView);
            }

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
            // Load patient view if not already loaded
            if (patientView.getChildren().isEmpty()) {
                AddContentToView("/fxml/patient.fxml", patientView);
            }

            // Switch views with fade transition
            switchToView(patientView);
        } catch (IOException e) {
            log.error("Error loading patient view", e);
        }
    }

    /**
     * Show the new recipe form
     */
    @FXML
    public void showNewRecipe() {
        try {
            // Load recipe view if not already loaded
            if (recipeView.getChildren().isEmpty()) {
                AddContentToView("/fxml/recipe.fxml", recipeView);
            }

            // Switch views with fade transition
            switchToView(recipeView);
        } catch (IOException e) {
            log.error("Error loading recipe view", e);
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
        } else if (recipeView.isVisible()) {
            currentView = recipeView;
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
}
