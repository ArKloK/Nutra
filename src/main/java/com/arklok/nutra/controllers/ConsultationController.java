package com.arklok.nutra.controllers;

import com.arklok.nutra.interfaces.IController;
import com.arklok.nutra.models.Consultation;
import com.arklok.nutra.models.Patient;
import com.arklok.nutra.services.ConsultationService;
import com.arklok.nutra.services.PatientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.arklok.nutra.helpers.ControllersHelper.showAlert;

@Component
public class ConsultationController implements IController {

    @FXML
    private ComboBox<Patient> patientComboBox;

    @FXML
    private DatePicker consultationDatePicker;

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private TextField weightField;

    @FXML
    private TextArea reasonArea;

    @FXML
    private TextArea notesArea;

    private HomeController homeController;
    private final ConsultationService consultationService;
    private final PatientService patientService;
    private Consultation currentConsultation; // For edit mode

    public ConsultationController(ConsultationService consultationService, PatientService patientService) {
        this.consultationService = consultationService;
        this.patientService = patientService;
    }

    public void initialize() {
        // Initialize date picker with current date
        consultationDatePicker.setValue(LocalDate.now());

        // Initialize time spinners
        if (hourSpinner != null) {
            SpinnerValueFactory<Integer> hourValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour());
            hourSpinner.setValueFactory(hourValueFactory);
            hourSpinner.setEditable(true);
        }

        if (minuteSpinner != null) {
            SpinnerValueFactory<Integer> minuteValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute());
            minuteSpinner.setValueFactory(minuteValueFactory);
            minuteSpinner.setEditable(true);
        }

        // Load patients from database
        loadPatients();
    }

    /**
     * Load patients into the combo box
     */
    private void loadPatients() {
        List<Patient> patients = patientService.findAll();
        patientComboBox.getItems().clear();
        patientComboBox.getItems().addAll(patients);

        // Configure how patients are displayed in the combo box
        patientComboBox.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getFirstName() + " " + patient.getLastName());
                }
            }
        });

        patientComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getFirstName() + " " + patient.getLastName());
                }
            }
        });
    }

    /**
     * Set the home controller reference to navigate back
     */
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Set a preselected date for the consultation
     */
    public void setPreselectedDate(LocalDate date) {
        if (consultationDatePicker != null) {
            consultationDatePicker.setValue(date);
        }
    }

    /**
     * Set an existing consultation for editing
     */
    public void setConsultationForEdit(Consultation consultation) {
        this.currentConsultation = consultation;

        // Load consultation data into form fields
        if (consultation != null) {
            // Set patient
            patientComboBox.setValue(consultation.getPatient());

            // Set date and time
            LocalDateTime dateTime = consultation.getDateTime();
            consultationDatePicker.setValue(dateTime.toLocalDate());

            if (hourSpinner != null) {
                hourSpinner.getValueFactory().setValue(dateTime.getHour());
            }

            if (minuteSpinner != null) {
                minuteSpinner.getValueFactory().setValue(dateTime.getMinute());
            }

            // Set weight
            if (consultation.getWeight() != null) {
                weightField.setText(String.valueOf(consultation.getWeight()));
            }

            // Set reason and notes
            reasonArea.setText(consultation.getReason());
            notesArea.setText(consultation.getNotes() != null ? consultation.getNotes() : "");
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
     * Save the consultation
     */
    @FXML
    public void saveConsultation() {
        // Validate required fields
        if (patientComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, selecciona un paciente");
            return;
        }

        if (consultationDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, selecciona una fecha");
            return;
        }

        if (reasonArea.getText() == null || reasonArea.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor, ingresa el motivo de la consulta");
            return;
        }

        try {
            // Use existing consultation if in edit mode, otherwise create new one
            Consultation consultation = currentConsultation != null ? currentConsultation : new Consultation();

            consultation.setPatient(patientComboBox.getValue());

            // Set date and time
            LocalDate date = consultationDatePicker.getValue();
            int hour = hourSpinner != null ? hourSpinner.getValue() : 9;
            int minute = minuteSpinner != null ? minuteSpinner.getValue() : 0;
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
            consultation.setDateTime(dateTime);

            consultation.setReason(reasonArea.getText().trim());
            consultation.setNotes(notesArea.getText() != null ? notesArea.getText().trim() : "");

            // Set weight if provided
            if (weightField.getText() != null && !weightField.getText().trim().isEmpty()) {
                try {
                    Float weight = Float.parseFloat(weightField.getText().trim());
                    consultation.setWeight(weight);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "El peso debe ser un número válido");
                    return;
                }
            } else {
                consultation.setWeight(null);
            }

            // Save consultation to database
            consultationService.save(consultation);

            String message = currentConsultation != null ? "Consulta actualizada correctamente" : "Consulta guardada correctamente";
            showAlert(Alert.AlertType.INFORMATION, "Éxito", message);

            // Reload consultations in home controller
            if (homeController != null) {
                homeController.reloadConsultations();
            }

            // Go back to calendar view
            goBack();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error al guardar la consulta: " + e.getMessage());
        }
    }

    /**
     * Cancel the consultation creation
     */
    @FXML
    public void cancelConsultation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cancelación");
        alert.setHeaderText("¿Cancelar la consulta?");
        alert.setContentText("Los cambios no guardados se perderán.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                goBack();
            }
        });
    }
}

