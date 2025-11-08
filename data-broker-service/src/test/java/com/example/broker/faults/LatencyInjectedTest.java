package com.example.broker.faults;

import com.example.broker.config.TestConfig;
import com.example.broker.persona.PersonaReservation;
import com.example.broker.persona.PersonaReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
class LatencyInjectedTest {

    @Autowired
    private PersonaReservationRepository repository;

    @Test
    void shouldHandleBasicOperations() {
        // given
        PersonaReservation reservation = new PersonaReservation();
        reservation.setPersonaId("test-persona-1");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");

        // when
        PersonaReservation saved = repository.save(reservation);

        // then
        Optional<PersonaReservation> found = repository.findByPersonaId("test-persona-1");
        assertThat(found).isPresent();
        assertThat(found.get().getPersonaId()).isEqualTo("test-persona-1");
    }
}
