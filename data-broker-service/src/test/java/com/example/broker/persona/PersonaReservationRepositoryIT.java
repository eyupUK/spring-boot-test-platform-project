package com.example.broker.persona;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PersonaReservationRepositoryIT {

    @Autowired
    private PersonaReservationRepository repository;

    @Test
    void shouldSaveAndRetrievePersonaReservation() {
        // given
        PersonaReservation reservation = PersonaReservation.builder()
                .personaId("test-persona-1")
                .reservedBy("test-user")
                .runId("test-run-1")
                .build();

        // when
        PersonaReservation saved = repository.save(reservation);

        // then
        Optional<PersonaReservation> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPersonaId()).isEqualTo("test-persona-1");
        assertThat(found.get().getReservedBy()).isEqualTo("test-user");
        assertThat(found.get().getRunId()).isEqualTo("test-run-1");
    }

    @Test
    void shouldFindByRunId() {
        // given
        PersonaReservation reservation1 = PersonaReservation.builder()
                .personaId("test-persona-1")
                .reservedBy("test-user")
                .runId("test-run-1")
                .build();

        PersonaReservation reservation2 = PersonaReservation.builder()
                .personaId("test-persona-2")
                .reservedBy("test-user")
                .runId("test-run-1")
                .build();

        repository.saveAll(List.of(reservation1, reservation2));

        // when
        List<PersonaReservation> found = repository.findByRunId("test-run-1");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(PersonaReservation::getRunId)
                .containsOnly("test-run-1");
    }

    @Test
    void shouldFindByPersonaId() {
        // given
        PersonaReservation reservation = PersonaReservation.builder()
                .personaId("test-persona-1")
                .reservedBy("test-user")
                .runId("test-run-1")
                .build();

        repository.save(reservation);

        // when
        Optional<PersonaReservation> found = repository.findByPersonaId("test-persona-1");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getPersonaId()).isEqualTo("test-persona-1");
    }

    @Test
    void shouldDeleteByRunId() {
        // given
        PersonaReservation reservation = PersonaReservation.builder()
                .personaId("test-persona-1")
                .reservedBy("test-user")
                .runId("test-run-1")
                .build();

        repository.save(reservation);

        // when
        repository.deleteByRunId("test-run-1");

        // then
        List<PersonaReservation> remaining = repository.findByRunId("test-run-1");
        assertThat(remaining).isEmpty();
    }
}
