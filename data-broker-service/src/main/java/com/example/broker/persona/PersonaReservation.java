package com.example.broker.persona;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persona_reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PersonaReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String personaId;

    private String runId;
    private String reservedBy;
    private String email;
    private String fullName;
    private String country;
}
