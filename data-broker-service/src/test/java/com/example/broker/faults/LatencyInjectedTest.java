package com.example.broker.faults;

import com.example.broker.config.TestConfig;
import com.example.broker.persona.PersonaReservation;
import com.example.broker.persona.PersonaReservationRepository;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
class LatencyInjectedTest {

    @Autowired
    private PersonaReservationRepository repository;

    private ToxiproxyClient toxiproxyClient;
    private Proxy dbProxy;

    @BeforeEach
    void setUp() throws IOException {
        toxiproxyClient = new ToxiproxyClient("localhost", 8474);
        dbProxy = toxiproxyClient.getProxy("db");
    }

    @Test
    void shouldHandleDatabaseLatency() throws IOException {
        // given
        PersonaReservation reservation = new PersonaReservation();
        reservation.setPersonaId("test-persona-1");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");
        repository.save(reservation);

        // Add 2 second latency
        dbProxy.toxics()
                .latency("latency", ToxicDirection.DOWNSTREAM, 2000)
                .setJitter(100)
                .setLatency(2000);

        // when/then
        assertThatThrownBy(() -> {
            Optional<PersonaReservation> result = repository.findByPersonaId("test-persona-1");
            result.isPresent(); // Force the query to execute
        }).hasMessageContaining("timeout");

        // Remove the latency
        dbProxy.toxics().get("latency").remove();

        // Verify normal operation is restored
        Optional<PersonaReservation> found = repository.findByPersonaId("test-persona-1");
        assertThat(found).isPresent();
        assertThat(found.get().getPersonaId()).isEqualTo("test-persona-1");
    }
}
