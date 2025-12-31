package com.arklok.nutra.controllers;

import com.arklok.nutra.helpers.ImageLoader;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Patient;
import com.arklok.nutra.services.PatientService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.arklok.nutra.helpers.ControllersHelper.showAlert;

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

    @FXML
    private javafx.scene.text.Text titleText;

    private HomeController homeController;
    private final PatientService patientService;
    private Patient currentPatient; // For edit mode
    private boolean isEditMode = false;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    public void initialize() {
        log.info("Patient controller initialized");
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Set patient for edit mode
     */
    public void setPatient(Patient patient) {
        this.currentPatient = patient;
        this.isEditMode = true;
        loadPatientData();
    }

    /**
     * Load patient data into form fields
     */
    private void loadPatientData() {
        if (currentPatient == null) {
            return;
        }

        log.info("Loading patient data for edit: {} {}", currentPatient.getFirstName(), currentPatient.getLastName());

        // Update title for edit mode
        if (titleText != null) {
            titleText.setText("Editar Paciente");
        }

        firstNameField.setText(currentPatient.getFirstName());
        lastNameField.setText(currentPatient.getLastName());

        if (currentPatient.getBirthDate() != null) {
            birthDatePicker.setValue(currentPatient.getBirthDate());
        }

        if (currentPatient.getEmail() != null) {
            emailField.setText(currentPatient.getEmail());
        }

        if (currentPatient.getPhone() != null) {
            phoneField.setText(currentPatient.getPhone());
        }

        if (currentPatient.getAddress() != null) {
            addressField.setText(currentPatient.getAddress());
        }

        if (currentPatient.getCurrentWeight() != null) {
            currentWeightField.setText(String.valueOf(currentPatient.getCurrentWeight()));
        }

        if (currentPatient.getMedicalRecordPath() != null) {
            medicalRecordPathField.setText(currentPatient.getMedicalRecordPath());
        }

        if (currentPatient.getPhotoPath() != null) {
            photoPathField.setText(currentPatient.getPhotoPath());
        }
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
                new FileChooser.ExtensionFilter("Imágenes compatibles", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File file = fileChooser.showOpenDialog(photoPathField.getScene().getWindow());
        if (file != null) {
            // Validate image format
            if (!ImageLoader.isSupportedFormat(file.getAbsolutePath())) {
                showAlert(Alert.AlertType.WARNING, "Formato no soportado",
                        ImageLoader.getUnsupportedFormatMessage(file.getAbsolutePath()));
                return;
            }
            photoPathField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Save the patient (create or update)
     */
    @FXML
    public void savePatient() {
        // Validate required fields
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, rellena el nombre");
            return;
        }
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, rellena el apellido");
            return;
        }
        if (birthDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, selecciona una fecha de nacimiento");
            return;
        }

        try {
            // Use existing patient for edit mode, or create new one
            Patient patient = isEditMode ? currentPatient : new Patient();

            patient.setFirstName(firstNameField.getText().trim());
            patient.setLastName(lastNameField.getText().trim());
            patient.setBirthDate(birthDatePicker.getValue());

            // Set optional fields
            if (emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
                patient.setEmail(emailField.getText().trim());
            }

            if (phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) {
                patient.setPhone(phoneField.getText().trim());
            }

            if (addressField.getText() != null && !addressField.getText().trim().isEmpty()) {
                patient.setAddress(addressField.getText().trim());
            }

            if (medicalRecordPathField.getText() != null && !medicalRecordPathField.getText().trim().isEmpty()) {
                patient.setMedicalRecordPath(medicalRecordPathField.getText().trim());
            }

            if (photoPathField.getText() != null && !photoPathField.getText().trim().isEmpty()) {
                patient.setPhotoPath(photoPathField.getText().trim());
            }

            // Parse and set current weight if provided
            if (currentWeightField.getText() != null && !currentWeightField.getText().trim().isEmpty()) {
                try {
                    float weight = Float.parseFloat(currentWeightField.getText().trim());
                    if (weight <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "El peso debe ser un número positivo");
                        return;
                    }
                    patient.setCurrentWeight(weight);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "El peso debe ser un número válido");
                    return;
                }
            }

            // Save patient to database
            Patient savedPatient = patientService.save(patient);

            String action = isEditMode ? "actualizado" : "guardado";
            log.info("Patient {} successfully: {} {} (ID: {})",
                    action,
                    savedPatient.getFirstName(),
                    savedPatient.getLastName(),
                    savedPatient.getId());

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "Paciente " + action + " correctamente: " + savedPatient.getFirstName() + " " + savedPatient.getLastName());

            // Go back to patient list view
            goBack();

        } catch (Exception e) {
            log.error("Error saving patient", e);
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error al guardar el paciente: " + e.getMessage());
        }
    }

    /**
     * Cancel patient creation
     */
    @FXML
    public void cancelPatient() {
        goBack();
    }

    /**
     * Go back to patient list view
     */
    @FXML
    public void goBack() {
        if (homeController != null) {
            homeController.showPatientList();
        }
    }
}

