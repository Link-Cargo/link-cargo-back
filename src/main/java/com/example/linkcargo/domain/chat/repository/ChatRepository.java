package com.example.linkcargo.domain.chat.repository;

import com.example.linkcargo.domain.chat.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
