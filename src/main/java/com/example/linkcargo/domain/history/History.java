package com.example.linkcargo.domain.history;

import com.example.linkcargo.domain.forwarding.Forwarding;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "historys")
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forwarding_id")
    private Forwarding forwarding;

    @Column(name = "quotation_id")
    private String quotationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String category;
}
