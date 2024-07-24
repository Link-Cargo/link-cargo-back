package com.example.linkcargo.domain.forwarding;

import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forwardings")
public class Forwarding extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "business_number", unique = true, nullable = false)
    private String businessNumber;


    @OneToMany(mappedBy = "forwarding", cascade = CascadeType.ALL)
    @Builder.Default
    private List<User> users = new ArrayList<>();

}
