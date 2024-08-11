package com.example.linkcargo.domain.chat.repository;

import com.example.linkcargo.domain.chat.Entity.Membership;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberShipRepository extends JpaRepository<Membership, Long> {

    boolean existsByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    /**
     * 특정 membership 에서, 특정 userId 가 아닌 다른 membership - 대화 상대 찾을 때 사용
     */
    @Query("SELECT m FROM Membership m WHERE m.chatRoom.id = :chatRoomId AND m.user.id <> :userId")
    List<Membership> findMembershipsByChatRoomIdAndExcludeUser(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}
