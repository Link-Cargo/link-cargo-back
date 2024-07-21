package com.example.linkcargo.domain.schedule;

import com.example.linkcargo.global.entity.BaseEntity;
import com.example.linkcargo.domain.port.Port;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedules")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "export_port_id")
    private Port exportPort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_port_id")
    private Port importPort;

    @Column(nullable = false)
    private String carrier;

    @Column(name = "etd")
    private LocalDateTime ETD;

    @Column(name = "eta")
    private LocalDateTime ETA;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    private TransportType transportType;

    @Column(name = "transit_time")
    private Integer transitTime;

    @Column(name = "document_cut_off")
    private LocalDateTime documentCutOff;

    @Column(name = "cargo_cut_off")
    private LocalDateTime cargoCutOff;


}
