package com.arklok.nutra.controllers;

import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Consultation;
import com.arklok.nutra.models.Patient;
import com.arklok.nutra.services.PatientService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class PatientDetailController implements IController {

    private static final Logger log = LoggerFactory.getLogger(PatientDetailController.class);

    @FXML
    private Text patientNameTitle;

    @FXML
    private ImageView patientPhoto;

    @FXML
    private Text fullNameText;

    @FXML
    private Text birthDateText;

    @FXML
    private Text ageText;

    @FXML
    private Text emailText;

    @FXML
    private Text phoneText;

    @FXML
    private Text addressText;

    @FXML
    private Text weightText;

    @FXML
    private Text medicalRecordText;

    @FXML
    private VBox consultationsContainer;

    private HomeController homeController;
    private PatientListController patientListController;
    private final PatientService patientService;
    private Patient currentPatient;

    public PatientDetailController(PatientService patientService) {
        this.patientService = patientService;
    }

    public void initialize() {
        log.info("Patient detail controller initialized");
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setPatientListController(PatientListController patientListController) {
        this.patientListController = patientListController;
    }

    /**
     * Set the patient to display
     */
    public void setPatient(Patient patient) {
        // Reload patient from database to ensure consultations are loaded
        if (patient.getId() != null) {
            this.currentPatient = patientService.findByIdWithConsultations(patient.getId()).orElse(patient);
        } else {
            this.currentPatient = patient;
        }
        loadPatientDetails();
    }

    /**
     * Load patient details into the view
     */
    private void loadPatientDetails() {
        if (currentPatient == null) {
            return;
        }

        // Set title
        String fullName = currentPatient.getFirstName() + " " + currentPatient.getLastName();
        patientNameTitle.setText(fullName);
        fullNameText.setText(fullName);

        // Set photo or placeholder
        loadPatientPhoto();

        // Birth date
        if (currentPatient.getBirthDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            birthDateText.setText(currentPatient.getBirthDate().format(formatter));

            // Calculate and set age
            int age = Period.between(currentPatient.getBirthDate(), LocalDate.now()).getYears();
            ageText.setText(age + " años");
        } else {
            birthDateText.setText("No especificada");
            ageText.setText("-");
        }

        // Contact info
        emailText.setText(currentPatient.getEmail() != null && !currentPatient.getEmail().isEmpty()
                ? currentPatient.getEmail()
                : "No especificado");

        phoneText.setText(currentPatient.getPhone() != null && !currentPatient.getPhone().isEmpty()
                ? currentPatient.getPhone()
                : "No especificado");

        addressText.setText(currentPatient.getAddress() != null && !currentPatient.getAddress().isEmpty()
                ? currentPatient.getAddress()
                : "No especificada");

        // Medical info
        if (currentPatient.getCurrentWeight() != null) {
            weightText.setText(String.format("%.1f kg", currentPatient.getCurrentWeight()));
        } else {
            weightText.setText("No especificado");
        }

        medicalRecordText.setText(currentPatient.getMedicalRecordPath() != null && !currentPatient.getMedicalRecordPath().isEmpty()
                ? new File(currentPatient.getMedicalRecordPath()).getName()
                : "Sin archivo");

        // Load consultations
        loadConsultations();
    }

    /**
     * Load patient photo or show placeholder
     */
    private void loadPatientPhoto() {
        if (currentPatient.getPhotoPath() != null && !currentPatient.getPhotoPath().isEmpty()) {
            File photoFile = new File(currentPatient.getPhotoPath());
            if (photoFile.exists()) {
                try {
                    Image image = new Image(photoFile.toURI().toString());
                    patientPhoto.setImage(image);
                    patientPhoto.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2); -fx-background-radius: 8px;");
                    return;
                } catch (Exception e) {
                    log.error("Error loading patient photo", e);
                }
            }
        }

        // Show black placeholder if no photo
        patientPhoto.setImage(null);
        Rectangle placeholder = new Rectangle(200, 200);
        placeholder.setFill(Color.web("#000000"));
        placeholder.setArcWidth(8);
        placeholder.setArcHeight(8);
        patientPhoto.setClip(placeholder);
        patientPhoto.setStyle("-fx-background-color: #000000; -fx-background-radius: 8px;");
    }

    /**
     * Load consultations for this patient
     */
    private void loadConsultations() {
        consultationsContainer.getChildren().clear();

        Set<Consultation> consultations = currentPatient.getConsultations();

        if (consultations == null || consultations.isEmpty()) {
            Text noConsultations = new Text("Sin consultas registradas");
            noConsultations.setStyle("-fx-font-size: 13px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");
            consultationsContainer.getChildren().add(noConsultations);
            return;
        }

        // Display consultations
        for (Consultation consultation : consultations) {
            HBox consultationRow = createConsultationRow(consultation);
            consultationsContainer.getChildren().add(consultationRow);
        }
    }

    /**
     * Create a row for a consultation
     */
    private HBox createConsultationRow(Consultation consultation) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5px; " +
                "-fx-border-color: " + UIConstants.COLOR_ACCENT + "; -fx-border-radius: 5px; -fx-border-width: 1px;");

        // Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Text dateText = new Text(consultation.getDateTime().format(formatter));
        dateText.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");
        row.getChildren().add(dateText);

        // Reason
        if (!consultation.getReason().isEmpty()) {
            Text reasonText = new Text(consultation.getReason());
            reasonText.setStyle("-fx-font-size: 13px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");
            row.getChildren().add(reasonText);
        }

        return row;
    }

    /**
     * Edit patient
     */
    @FXML
    public void editPatient() {
        log.info("Edit patient button clicked for: {} {}", currentPatient.getFirstName(), currentPatient.getLastName());
        if (homeController != null && currentPatient != null) {
            homeController.showEditPatient(currentPatient, this);
        } else {
            log.error("Cannot edit patient: homeController or currentPatient is null");
        }
    }

    /**
     * Go back to patient list
     */
    @FXML
    public void goBack() {
        log.info("Go back button clicked from patient detail");
        if (patientListController != null) {
            // Go back to patient list
            log.info("Going back to patient list");
            if (homeController != null) {
                homeController.showPatientList();
            } else {
                log.error("HomeController is null! Cannot go back to patient list.");
            }
        } else if (homeController != null) {
            // Go back to calendar
            log.info("Going back to calendar");
            homeController.showCalendarView();
        } else {
            log.error("Both homeController and patientListController are null!");
        }
    }
}

