package com.example.broker.persona;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PersonaController {

    private final PersonaReservationRepository repo;
    private static final String[] COUNTRIES = {"GB","DE","FR","US","TR"};

    @PostMapping("/personas/{personaId}/reservations")
    public ResponseEntity<?> reservePersona(@PathVariable String personaId, @RequestBody PersonaReservation request) {
        return repo.findByPersonaId(personaId)
                .map(existing -> ResponseEntity.status(409).build())
                .orElseGet(() -> {
                    var saved = repo.save(request);
                    return ResponseEntity.status(201).body(saved);
                });
    }

    @GetMapping("/personas/reservations")
    public List<PersonaReservation> getReservations(@RequestParam String runId) {
        return repo.findByRunId(runId).stream().toList();
    }

    @DeleteMapping("/personas/reservations")
    public ResponseEntity<?> deleteReservations(@RequestParam String runId) {
        repo.deleteByRunId(runId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/personas/reserve")
    public ResponseEntity<?> reserve() {
        var rnd = new Random();
        var name = "Test User " + (1000 + rnd.nextInt(9000));
        var email = ("user" + System.currentTimeMillis() + "@example.com");
        var pr = PersonaReservation.builder()
                .fullName(name).email(email).country(COUNTRIES[rnd.nextInt(COUNTRIES.length)])
                .build();
        repo.save(pr);
        return ResponseEntity.ok(Map.of("id", pr.getId(), "email", pr.getEmail(), "name", pr.getFullName(), "country", pr.getCountry()));
    }
}
