package com.arklok.nutra.repositories;

import com.arklok.nutra.models.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    /**
     * Find all consultations between two dates
     */
    @Query("SELECT c FROM Consultation c WHERE c.dateTime >= :startDate AND c.dateTime < :endDate ORDER BY c.dateTime ASC")
    List<Consultation> findByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find all consultations for a specific day
     */
    @Query("SELECT c FROM Consultation c WHERE c.dateTime >= :startOfDay AND c.dateTime < :endOfDay ORDER BY c.dateTime ASC")
    List<Consultation> findByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}


