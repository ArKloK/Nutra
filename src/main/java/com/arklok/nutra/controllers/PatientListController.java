package com.arklok.nutra.controllers;

import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.helpers.ImageLoader;
import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Patient;
import com.arklok.nutra.services.PatientService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.arklok.nutra.controllers.IngredientListController.gethBox;

@Component
public class PatientListController implements IController {

    private static final Logger log = LoggerFactory.getLogger(PatientListController.class);

    @FXML
    private TextField searchField;

    @FXML
    private VBox patientListContainer;

    private HomeController homeController;
    private final PatientService patientService;

    public PatientListController(PatientService patientService) {
        this.patientService = patientService;
    }

    public void initialize() {
        log.info("Patient list controller initialized");

        // Load all patients initially
        loadPatients();

        // Setup search filter
        searchField.textProperty().addListener((_, _, newValue) -> filterPatients(newValue));
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Load all patients and display them as cards
     */
    private void loadPatients() {
        try {
            List<Patient> patients = patientService.findAll();
            displayPatients(patients);
        } catch (Exception e) {
            log.error("Error loading patients", e);
        }
    }

    /**
     * Filter patients by search term
     */
    private void filterPatients(String searchTerm) {
        try {
            List<Patient> patients;
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                patients = patientService.findAll();
            } else {
                patients = patientService.searchByName(searchTerm);
            }
            displayPatients(patients);
        } catch (Exception e) {
            log.error("Error filtering patients", e);
        }
    }

    /**
     * Display patients as cards
     */
    private void displayPatients(List<Patient> patients) {
        patientListContainer.getChildren().clear();

        if (patients == null || patients.isEmpty()) {
            // Show empty state
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(40));

            Text emptyText = new Text("No se encontraron pacientes");
            emptyText.setStyle("-fx-font-size: 16px; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

            emptyState.getChildren().add(emptyText);
            patientListContainer.getChildren().add(emptyState);
            return;
        }

        // Create a card for each patient
        for (Patient patient : patients) {
            VBox patientCard = createPatientCard(patient);
            patientListContainer.getChildren().add(patientCard);
        }
    }

    /**
     * Create a card for a patient
     */
    private VBox createPatientCard(Patient patient) {
        VBox card = new VBox(15);
        card.getStyleClass().add("patient-card");
        card.setPadding(new Insets(20));

        // Main content container with photo and info
        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER_LEFT);

        // Photo section
        StackPane photoContainer = createPhotoContainer(patient);
        mainContent.getChildren().add(photoContainer);

        // Info section
        VBox infoSection = new VBox(10);
        HBox.setHgrow(infoSection, javafx.scene.layout.Priority.ALWAYS);

        // Header with name and age
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Text nameText = new Text(patient.getFirstName() + " " + patient.getLastName());
        nameText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: " + UIConstants.COLOR_PRIMARY + ";");

        // Calculate age if birthdate is available
        if (patient.getBirthDate() != null) {
            int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
            Text ageText = new Text(age + " años");
            ageText.setStyle("-fx-font-size: 14px; -fx-fill: " + UIConstants.COLOR_SECONDARY + ";");
            header.getChildren().addAll(nameText, ageText);
        } else {
            header.getChildren().add(nameText);
        }

        infoSection.getChildren().add(header);

        // Patient details section
        VBox detailsBox = new VBox(8);
        detailsBox.setPadding(new Insets(5, 0, 0, 0));

        // Email
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            HBox emailRow = createInfoRow("Email:", patient.getEmail());
            detailsBox.getChildren().add(emailRow);
        }

        // Phone
        if (patient.getPhone() != null && !patient.getPhone().isEmpty()) {
            HBox phoneRow = createInfoRow("Teléfono:", patient.getPhone());
            detailsBox.getChildren().add(phoneRow);
        }

        // Birth date
        if (patient.getBirthDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String birthDateStr = patient.getBirthDate().format(formatter);
            HBox birthDateRow = createInfoRow("Fecha de Nacimiento:", birthDateStr);
            detailsBox.getChildren().add(birthDateRow);
        }

        // Current weight
        if (patient.getCurrentWeight() != null) {
            HBox weightRow = createInfoRow("Peso Actual:", String.format("%.1f kg", patient.getCurrentWeight()));
            detailsBox.getChildren().add(weightRow);
        }

        infoSection.getChildren().add(detailsBox);
        mainContent.getChildren().add(infoSection);

        card.getChildren().add(mainContent);

        // Add hover effect and click handler
        card.setOnMouseEntered(_ -> card.setStyle("-fx-cursor: hand;"));
        card.setOnMouseExited(_ -> card.setStyle("-fx-cursor: default;"));
        card.setOnMouseClicked(_ -> showPatientDetail(patient));

        return card;
    }

    /**
     * Create photo container with image or black placeholder
     */
    private StackPane createPhotoContainer(Patient patient) {
        StackPane photoContainer = new StackPane();
        photoContainer.setPrefSize(100, 100);
        photoContainer.setMinSize(100, 100);
        photoContainer.setMaxSize(100, 100);

        if (patient.getPhotoPath() != null && !patient.getPhotoPath().isEmpty()) {
            log.info("Attempting to load patient list photo from: {}", patient.getPhotoPath());

            Image image = ImageLoader.loadImage(patient.getPhotoPath());

            if (image != null) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);
                imageView.setCache(true);

                Rectangle clip = new Rectangle(100, 100);
                clip.setArcWidth(8);
                clip.setArcHeight(8);
                imageView.setClip(clip);

                photoContainer.getChildren().add(imageView);
                photoContainer.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);");
                log.info("Patient list photo loaded successfully");
                return photoContainer;
            }
        }

        // Black placeholder
        Rectangle placeholder = new Rectangle(100, 100);
        placeholder.setArcWidth(8);
        placeholder.setArcHeight(8);
        placeholder.setFill(javafx.scene.paint.Color.web("#000000"));
        photoContainer.getChildren().add(placeholder);
        photoContainer.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        return photoContainer;
    }

    /**
     * Show patient detail view
     */
    private void showPatientDetail(Patient patient) {
        log.info("Showing patient detail for: {} {}", patient.getFirstName(), patient.getLastName());
        if (homeController != null) {
            homeController.showPatientDetail(patient, this);
        } else {
            log.error("HomeController is null! Cannot show patient detail.");
        }
    }

    /**
     * Create an info row with label and value
     */
    private HBox createInfoRow(String label, String value) {
        return gethBox(label, value);
    }

    /**
     * Show new patient form
     */
    @FXML
    public void showNewPatient() {
        log.info("Show new patient button clicked");
        if (homeController != null) {
            homeController.showNewPatient();
        } else {
            log.error("HomeController is null! Cannot show new patient form.");
        }
    }

    /**
     * Go back to calendar view
     */
    @FXML
    public void goBack() {
        log.info("Go back button clicked");
        if (homeController != null) {
            homeController.showCalendarView();
        } else {
            log.error("HomeController is null! Cannot go back.");
        }
    }
}

