package com.example.linkcargo.domain.forwarding;

import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.entity.JpaBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "forwardings")
public class Forwarding extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String firmName;

    @Column(name = "business_number", unique = true, nullable = false)
    private String businessNumber;

    @Column(name = "firm_logo")
    private String firmLogoImageUrl;

    @Column(name = "main_subject")
    private String mainSubject;

    @Column(name = "firm_url")
    private String firmUrl;

    @Column(name = "firm_address")
    private String firmAddress;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "ceo")
    private String firmCeo;

    @Column(name = "tel")
    private String firmTel;

    @OneToMany(mappedBy = "forwarding", cascade = CascadeType.ALL)
    @Builder.Default
    private List<User> users = new ArrayList<>();

}
