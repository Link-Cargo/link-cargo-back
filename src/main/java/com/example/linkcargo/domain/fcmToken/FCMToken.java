package com.example.linkcargo.domain.fcmToken;

import com.example.linkcargo.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Column(unique = true)
    private User user;

    private String token;

    public FCMToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void update(String token){
        this.token = token;
    }

    @Override
    public String toString() {
        return "FcmToken{" +
            "id=" + id +
            ", user=" + user +
            ", token='" + token + '\'' +
            '}';
    }
}
