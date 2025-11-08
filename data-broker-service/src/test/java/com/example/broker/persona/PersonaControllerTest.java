package com.example.broker.persona;

import com.example.broker.config.BaseTestConfiguration;
import com.example.broker.config.SecurityTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonaController.class)
@Import(SecurityTestConfig.class)
class PersonaControllerTest extends BaseTestConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonaReservationRepository repository;

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/personas/reservations")
                        .param("runId", "test-run-1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/personas/test-persona-1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/personas/reservations")
                        .param("runId", "test-run-1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldCreatePersonaReservation() throws Exception {
        // given
        PersonaReservation reservation = new PersonaReservation();
        reservation.setId(1L);
        reservation.setPersonaId("test-persona-1");
        reservation.setReservedBy("test-user");
        reservation.setRunId("test-run-1");

        when(repository.findByPersonaId("test-persona-1")).thenReturn(Optional.empty());
        when(repository.save(any(PersonaReservation.class))).thenReturn(reservation);

        // when/then
        mockMvc.perform(post("/api/personas/test-persona-1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.personaId").value("test-persona-1"))
                .andExpect(jsonPath("$.reservedBy").value("test-user"))
                .andExpect(jsonPath("$.runId").value("test-run-1"));
    }

    @Test
    @WithMockUser
    void shouldReturnConflictWhenPersonaAlreadyReserved() throws Exception {
        // given
        PersonaReservation existingReservation = new PersonaReservation();
        existingReservation.setPersonaId("test-persona-1");
        when(repository.findByPersonaId("test-persona-1")).thenReturn(Optional.of(existingReservation));

        PersonaReservation newReservation = new PersonaReservation();
        newReservation.setPersonaId("test-persona-1");

        // when/then
        mockMvc.perform(post("/api/personas/test-persona-1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReservation)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void shouldGetReservationsByRunId() throws Exception {
        // given
        PersonaReservation reservation = new PersonaReservation();
        reservation.setId(1L);
        reservation.setPersonaId("test-persona-1");
        reservation.setRunId("test-run-1");

        when(repository.findByRunId("test-run-1")).thenReturn(List.of(reservation));

        // when/then
        mockMvc.perform(get("/api/personas/reservations")
                        .param("runId", "test-run-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].personaId").value("test-persona-1"))
                .andExpect(jsonPath("$[0].runId").value("test-run-1"));
    }

    @Test
    @WithMockUser
    void shouldDeleteReservationsByRunId() throws Exception {
        // given
        String runId = "test-run-1";

        // when/then
        mockMvc.perform(delete("/api/personas/reservations")
                        .param("runId", runId))
                .andExpect(status().isNoContent());

        verify(repository).deleteByRunId(runId);
    }
}
