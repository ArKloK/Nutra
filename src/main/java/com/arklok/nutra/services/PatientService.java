package com.arklok.nutra.services;

import com.arklok.nutra.models.Patient;
import com.arklok.nutra.repositories.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Save a patient
     */
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Find a patient by id
     */
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Find all patients
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * Search patients by name
     */
    public List<Patient> searchByName(String searchTerm) {
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    /**
     * Delete a patient
     */
    public void delete(Patient patient) {
        patientRepository.delete(patient);
    }

    /**
     * Delete a patient by id
     */
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }
}
