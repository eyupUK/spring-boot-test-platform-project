package com.example.broker.contracts;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.example.broker.persona.PersonaReservation;
import com.example.broker.persona.PersonaReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("data-broker-service")
@PactFolder("../orchestrator-service/target/pacts")
@ActiveProfiles("test")
class DataBrokerPactProviderTest {

    @LocalServerPort
    private int port;

    @MockBean
    private PersonaReservationRepository repository;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("A persona is available")
    void toPersonaAvailableState() {
        PersonaReservation reservation = new PersonaReservation();
        reservation.setId(1L);
        reservation.setPersonaId("test-persona");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");

        when(repository.findByPersonaId("test-persona")).thenReturn(Optional.empty());
        when(repository.save(any(PersonaReservation.class))).thenReturn(reservation);
    }

    @State("Reservations exist for a run")
    void toReservationsExistState() {
        PersonaReservation reservation = new PersonaReservation();
        reservation.setId(1L);
        reservation.setPersonaId("test-persona");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");

        when(repository.findByRunId("test-run-1")).thenReturn(List.of(reservation));
    }
}
