package com.example.linkcargo.domain.notification;

import com.example.linkcargo.domain.notification.dto.NotificationDTO;
import com.example.linkcargo.domain.user.User;
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
import java.lang.reflect.Type;
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

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String buttonTitle;

    @Column
    private String buttonUrl;

    @Column(name = "is_read") // 읽음 여부
    private boolean isRead;

    public Notification(User user, NotificationType type, String title, String content, String buttonTitle, String buttonUrl) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
        this.buttonTitle = buttonTitle;
        this.buttonUrl = buttonUrl;
        this.isRead = false;
    }

    public NotificationDTO toNotificationDTO(){
        return new NotificationDTO(
            this.id,
            this.user.getId(),
            this.type,
            this.title,
            this.content,
            this.buttonTitle,
            this.buttonUrl,
            this.getCreatedAt(),
            this.isRead
        );
    }

    public void updateRead(boolean isRead) {
        this.isRead = isRead;
    }
}
