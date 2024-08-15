package com.example.linkcargo.domain.schedule;

import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.global.entity.JpaBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedules")
public class Schedule extends JpaBaseEntity {

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

    @Column(nullable = false)
    private String vessel;

    @Column(name = "etd")
    private LocalDateTime ETD;

    @Column(name = "eta")
    private LocalDateTime ETA;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    private TransportType transportType;

    @Column(name = "transit_time")
    private Integer transitTime;

    @Column(name = "limit_size")
    private Integer limitSize;

    @Column(name = "Qty")
    private Integer Qty;

    @Column(name = "CBM")
    private Integer limitCBM;

    @Column(name = "document_cut_off")
    private LocalDateTime documentCutOff;

    @Column(name = "cargo_cut_off")
    private LocalDateTime cargoCutOff;


}
