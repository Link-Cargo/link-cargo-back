package com.example.linkcargo.domain.chat.repository;

import com.example.linkcargo.domain.chat.Entity.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // TODO 이거 반대로 해야 프론트가 사용하기 편할듯, 채팅은 시간 순서대로 나열이니.
    List<Chat> findAllByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);
}
