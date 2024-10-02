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

    /**
     * 특정 채팅방의 특정 사용자가 보낸 채팅 목록을 반환
     */
    List<Chat> findAllByChatRoomIdAndSenderId(Long chatRoomId, Long senderId);

    /**
     * 특정 채팅방의 가장 최근의 채팅 조회 (없을 수도 있음)
     */
    Optional<Chat> findTopByChatRoomIdOrderByIdDesc(Long chatRoomId);

}
