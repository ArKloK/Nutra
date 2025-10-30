package com.arklok.nutra.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    private LocalDateTime dateTime;

    private Float weight;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // denormalized field to preserve patient name if patient deleted
    @Column(name = "patient_name")
    private String patientName;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Patient patient;

    public Consultation() {}

    @PrePersist
    @PreUpdate
    public void syncPatientName() {
        if (patient != null) {
            String fn = patient.getFirstName() != null ? patient.getFirstName() : "";
            String ln = patient.getLastName() != null ? " " + patient.getLastName() : "";
            this.patientName = (fn + ln).trim();
        }
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public Float getWeight() { return weight; }
    public void setWeight(Float weight) { this.weight = weight; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consultation that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
