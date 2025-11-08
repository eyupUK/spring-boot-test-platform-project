package com.example.broker.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PersonaReservationRepository extends JpaRepository<PersonaReservation, Long> {
    Optional<PersonaReservation> findByPersonaId(String personaId);

    List<PersonaReservation> findByRunId(String runId);

    @Transactional
    void deleteByRunId(String runId);
}
