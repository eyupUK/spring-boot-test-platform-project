package com.example.broker.persona;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PersonaReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String personaId;
    private String runId;
    private String reservedBy;
    private String email;
    private String fullName;
    private String country;
}
