package com.example.linkcargo.domain.chat.repository;

import com.example.linkcargo.domain.chat.Entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 특정 유저와 대상 유저 간의 채팅방을 찾는 쿼리
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "JOIN cr.memberships m1 " +
           "JOIN cr.memberships m2 " +
           "WHERE m1.user.id = :userId AND m2.user.id = :targetUserId")
    Optional<ChatRoom> findByUserIdAndTargetUserId(
        @Param("userId") Long userId,
        @Param("targetUserId") Long targetUserId);

    /**
     * 특정 유저가 속한 채팅방들을 찾는 쿼리
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "JOIN cr.memberships m " +
           "WHERE m.user.id = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") Long userId);
}
