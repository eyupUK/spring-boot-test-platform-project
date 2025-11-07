package com.example.broker.persona;

import com.example.broker.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PersonaReservationRepositoryTest {

    @Autowired
    private PersonaReservationRepository repository;

    @Test
    void shouldSaveAndRetrievePersonaReservation() {
        // given
        PersonaReservation reservation = new PersonaReservation();
        reservation.setPersonaId("test-persona-1");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");

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
        PersonaReservation reservation1 = new PersonaReservation();
        reservation1.setPersonaId("test-persona-1");
        reservation1.setReservedBy("test-user");
        reservation1.setRunId("test-run-1");

        PersonaReservation reservation2 = new PersonaReservation();
        reservation2.setPersonaId("test-persona-2");
        reservation2.setReservedBy("test-user");
        reservation2.setRunId("test-run-1");

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
        PersonaReservation reservation = new PersonaReservation();
        reservation.setPersonaId("test-persona-1");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");
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
        PersonaReservation reservation = new PersonaReservation();
        reservation.setPersonaId("test-persona-1");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");
        repository.save(reservation);

        // when
        repository.deleteByRunId("test-run-1");

        // then
        List<PersonaReservation> remaining = repository.findByRunId("test-run-1");
        assertThat(remaining).isEmpty();
    }
}
