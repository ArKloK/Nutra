package com.arklok.nutra.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String address;

    // file paths (recommended) - change to @Lob byte[] if you prefer storing binary data
    private String medicalRecordPath;
    private String photoPath;

    private Float currentWeight;

    @OneToMany(mappedBy = "patient",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false,
            fetch = FetchType.LAZY)
    private Set<Consultation> consultations = new HashSet<>();

    public Patient() {
    }

    // helpers
    public void addConsultation(Consultation c) {
        consultations.add(c);
        c.setPatient(this);
    }

    public void removeConsultation(Consultation c) {
        consultations.remove(c);
        //c.setPatient(null);
    }

    @PreRemove
    private void preRemove() {
        // before deleting the patient, detach consultations and preserve patient name on them
        for (Consultation c : new HashSet<>(consultations)) {
            //c.setPatient(null);
            String fullName = (firstName != null ? firstName : "") + (lastName != null ? " " + lastName : "");
            c.setPatientName(fullName.trim());
        }
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedicalRecordPath() {
        return medicalRecordPath;
    }

    public void setMedicalRecordPath(String medicalRecordPath) {
        this.medicalRecordPath = medicalRecordPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Float getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Float currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Set<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        this.consultations = consultations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return id != null && id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
