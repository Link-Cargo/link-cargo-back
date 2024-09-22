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
import java.lang.reflect.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.N;

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

    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    /*
    광고일때 ->  버튼을 클릭했을 때 이동할 주소
    메시지일때 -> 알림(전체)을 클릭했을 때 이동할 주소
     */
    @Column
    private String url;

    @Column // 광고일때만 사용
    private String buttonTitle;

    @Column(name = "is_read") // 읽음 여부
    private boolean isRead;


    // (일반) - 링크 이동하지 않는 알림
    public Notification(User user, NotificationType type, String title, String content) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
        this.isRead = false;
    }

    // (일반) - 링크 이동하는 알림
    public Notification(User user, NotificationType type, String title, String content, String url) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
        this.url = url;
        this.isRead = false;
    }

    // (광고) - 버튼을 눌렀을 때 링크 이동하는 알림
    public Notification(User user, NotificationType type, String title, String content, String url, String buttonTitle) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
        this.url = url;
        this.buttonTitle = buttonTitle;
        this.isRead = false;
    }

    public NotificationDTO toNotificationDTO(){
        return new NotificationDTO(
            this.id,
            this.user.getId(),
            this.type,
            this.title,
            this.content,
            this.url,
            this.buttonTitle,
            this.isRead
        );
    }

    public void updateRead(boolean isRead) {
        this.isRead = isRead;
    }
}
