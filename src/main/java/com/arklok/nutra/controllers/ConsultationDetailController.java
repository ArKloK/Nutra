package com.arklok.nutra.controllers;

import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Consultation;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ConsultationDetailController implements IController {

    private static final Logger log = LoggerFactory.getLogger(ConsultationDetailController.class);

    @FXML
    private Text patientNameText;

    @FXML
    private Text dateText;

    @FXML
    private Text timeText;

    @FXML
    private Text weightText;

    @FXML
    private Text reasonText;

    @FXML
    private Text notesText;

    @FXML
    private VBox measurementsSection;

    @FXML
    private VBox notesSection;

    private HomeController homeController;
    private Consultation currentConsultation;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Set the home controller reference to navigate back
     */
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Set the consultation to display
     */
    public void setConsultation(Consultation consultation) {
        this.currentConsultation = consultation;
        loadConsultationData();
    }

    /**
     * Load consultation data into the view
     */
    private void loadConsultationData() {
        if (currentConsultation == null) {
            log.warn("Attempted to load null consultation");
            return;
        }

        try {
            // Set patient name
            patientNameText.setText(currentConsultation.getPatientName());

            // Set date and time
            dateText.setText(currentConsultation.getDateTime().format(DATE_FORMATTER));
            timeText.setText(currentConsultation.getDateTime().format(TIME_FORMATTER));

            // Set weight if available
            if (currentConsultation.getWeight() != null) {
                weightText.setText(String.format("%.2f kg", currentConsultation.getWeight()));
                measurementsSection.setManaged(true);
                measurementsSection.setVisible(true);
            } else {
                measurementsSection.setManaged(false);
                measurementsSection.setVisible(false);
            }

            // Set reason
            reasonText.setText(currentConsultation.getReason());

            // Set notes if available
            if (currentConsultation.getNotes() != null && !currentConsultation.getNotes().trim().isEmpty()) {
                notesText.setText(currentConsultation.getNotes());
                notesSection.setManaged(true);
                notesSection.setVisible(true);
            } else {
                notesSection.setManaged(false);
                notesSection.setVisible(false);
            }

            log.info("Consultation data loaded successfully for consultation ID: {}", currentConsultation.getId());
        } catch (Exception e) {
            log.error("Error loading consultation data", e);
        }
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
     * Edit the current consultation
     */
    @FXML
    public void editConsultation() {
        if (homeController != null && currentConsultation != null) {
            homeController.showEditConsultation(currentConsultation);
        }
    }
}

