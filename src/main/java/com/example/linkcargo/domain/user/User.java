package com.example.linkcargo.domain.user;

import com.example.linkcargo.domain.chat.Membership;
import com.example.linkcargo.domain.forwarding.Forwarding;
import com.example.linkcargo.domain.notification.Notification;
import com.example.linkcargo.domain.user.dto.UserResponse;
import com.example.linkcargo.global.entity.JpaBaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status != 'DELETED'")
@SQLDelete(sql = "UPDATE linkcargo.users SET status = 'DELETED' WHERE id = ?")
@Table(name = "users")
public class User extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forwarding_id")
    private Forwarding forwarding; // 기본적으로 null

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "business_number", unique = true)
    private String businessNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Membership> memberships = new ArrayList<>();

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", role=" + role +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", companyName='" + companyName + '\'' +
               ", jobTitle='" + jobTitle + '\'' +
               ", businessNumber='" + businessNumber + '\'' +
               ", status=" + status +
               ", totalPrice=" + totalPrice +
               '}';
    }

    public UserResponse toUserResponse() {
        return new UserResponse(
            this.id,
            this.role,
            this.firstName,
            this.lastName,
            this.email,
            this.phoneNumber,
            this.companyName,
            this.jobTitle,
            this.businessNumber,
            this.status,
            this.totalPrice
        );
    }

    public void changeStatus(Status status) {
        this.status = status;
    }
}
