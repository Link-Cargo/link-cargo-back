package com.example.linkcargo.domain.notification;

import com.example.linkcargo.domain.notification.dto.NotificationDTO;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.entity.JpaBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "notifications")
public class Notification extends JpaBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column // 클릭 시 이동할 주소(현재는 필수 X)
    private String url;

    @Column(name = "is_read") // 읽음 여부
    private boolean isRead;

    public Notification(User user, String title, String content, boolean isRead) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }

    public Notification(User user, String title, String content, String url, boolean isRead) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }

    public NotificationDTO toNotificationDTO(){
        return new NotificationDTO(
            this.id,
            this.user.getId(),
            this.title,
            this.content,
            this.isRead
        );
    }

    public void updateRead(boolean isRead) {
        this.isRead = isRead;
    }
}
