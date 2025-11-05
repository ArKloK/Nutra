package com.arklok.nutra.controllers;

import com.arklok.nutra.interfaces.IController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PatientController implements IController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField currentWeightField;

    @FXML
    private TextField medicalRecordPathField;

    @FXML
    private TextField photoPathField;

    private HomeController homeController;

    public void initialize() {
        log.info("Patient controller initialized");
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Browse for medical record file
     */
    @FXML
    public void browseMedicalRecord() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Historial Médico");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Documentos Word", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File file = fileChooser.showOpenDialog(medicalRecordPathField.getScene().getWindow());
        if (file != null) {
            medicalRecordPathField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Browse for patient photo
     */
    @FXML
    public void browsePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Paciente");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File file = fileChooser.showOpenDialog(photoPathField.getScene().getWindow());
        if (file != null) {
            photoPathField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Save the patient
     */
    @FXML
    public void savePatient() {
        // Validate required fields
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            birthDatePicker.getValue() == null) {

            log.warn("Required fields are missing");
            // TODO: Show error message to user
            return;
        }

        // TODO: Create patient object and save to database
        log.info("Saving patient: {} {}", firstNameField.getText(), lastNameField.getText());

        // For now, just go back
        goBack();
    }

    /**
     * Cancel patient creation
     */
    @FXML
    public void cancelPatient() {
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
}

