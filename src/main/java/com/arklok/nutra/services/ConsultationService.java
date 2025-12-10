package com.arklok.nutra.services;

import com.arklok.nutra.models.Consultation;
import com.arklok.nutra.repositories.ConsultationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    /**
     * Save a consultation
     */
    public Consultation save(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    /**
     * Find a consultation by id
     */
    public Optional<Consultation> findById(Long id) {
        return consultationRepository.findById(id);
    }

    /**
     * Find all consultations
     */
    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    /**
     * Find consultations for a specific week
     */
    public List<Consultation> findByWeek(LocalDate weekStart) {
        LocalDateTime startDateTime = weekStart.atStartOfDay();
        LocalDateTime endDateTime = weekStart.plusWeeks(1).atStartOfDay();
        return consultationRepository.findByDateBetween(startDateTime, endDateTime);
    }

    /**
     * Find consultations for a specific day
     */
    public List<Consultation> findByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return consultationRepository.findByDate(startOfDay, endOfDay);
    }

    /**
     * Delete a consultation
     */
    public void delete(Consultation consultation) {
        consultationRepository.delete(consultation);
    }

    /**
     * Delete a consultation by id
     */
    public void deleteById(Long id) {
        consultationRepository.deleteById(id);
    }
}

