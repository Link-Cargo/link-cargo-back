package com.example.linkcargo.domain.notification;

import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);

    // 페이징
    Page<Notification> findAllByUserId(Long userId, PageRequest pageRequest);
    Page<Notification> findAllByUserIdAndIsReadFalse(Long userId, PageRequest pageRequest);
}
