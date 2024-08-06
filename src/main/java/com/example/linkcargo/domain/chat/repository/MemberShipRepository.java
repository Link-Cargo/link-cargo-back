package com.example.linkcargo.domain.chat.repository;

import com.example.linkcargo.domain.chat.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberShipRepository extends JpaRepository<Membership, Long> {

    boolean existsByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
