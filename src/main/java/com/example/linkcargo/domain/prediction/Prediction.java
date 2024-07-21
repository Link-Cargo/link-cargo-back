package com.example.linkcargo.domain.prediction;

import com.example.linkcargo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "predictions")
public class Prediction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scfi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType type;
}
