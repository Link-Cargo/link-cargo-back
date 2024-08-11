package com.example.linkcargo.domain.chat.repository;

import com.example.linkcargo.domain.chat.Entity.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    /**
     * 특정 채팅방의 가장 최근의 chat 을 반환
     */
    @Query("select c from Chat c where c.chatRoom.id = :chatRoomId order by c.createdAt desc")
    List<Chat> findLatestChatByChatRoomId(@Param("chatRoomId") Long chatRoomId);

}
