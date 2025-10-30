package com.arklok.nutra.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ConsultationController {

    @FXML
    private ComboBox<String> patientComboBox;

    @FXML
    private DatePicker consultationDatePicker;

    @FXML
    private TextField weightField;

    @FXML
    private TextArea reasonArea;

    @FXML
    private TextArea notesArea;

    private HomeController homeController;

    public void initialize() {
        // Initialize date picker with current date
        consultationDatePicker.setValue(LocalDate.now());

        // TODO: Load patients from database
        patientComboBox.getItems().addAll("Paciente de Ejemplo 1", "Paciente de Ejemplo 2");
    }

    /**
     * Set the home controller reference to navigate back
     */
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
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
     * Save the consultation
     */
    @FXML
    public void saveConsultation() {
        // Validate required fields
        if (patientComboBox.getValue() == null) {
            showAlert("Error", "Por favor, selecciona un paciente");
            return;
        }

        if (consultationDatePicker.getValue() == null) {
            showAlert("Error", "Por favor, selecciona una fecha");
            return;
        }

        // TODO: Save consultation to database
        showAlert("Éxito", "Consulta guardada correctamente");

        // Go back to calendar view
        goBack();
    }

    /**
     * Cancel the consultation creation
     */
    @FXML
    public void cancelConsultation() {
        // TODO: Show confirmation dialog
        goBack();
    }

    /**
     * Show an alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

