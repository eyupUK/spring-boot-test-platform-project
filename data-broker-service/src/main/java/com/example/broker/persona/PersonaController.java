package com.example.broker.persona;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class PersonaController {

  private final PersonaReservationRepository repo;
  private static final String[] COUNTRIES = {"GB","DE","FR","US","TR"};

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
