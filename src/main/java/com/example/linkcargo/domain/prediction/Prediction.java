package com.example.linkcargo.domain.prediction;

import com.example.linkcargo.global.entity.JpaBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "predictions")
public class Prediction extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scfi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType type;
}
