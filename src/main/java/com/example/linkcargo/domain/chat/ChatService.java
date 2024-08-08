package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.domain.chat.Entity.ChatRoom;
import com.example.linkcargo.domain.chat.Entity.Membership;
import com.example.linkcargo.domain.chat.Entity.RoomStatus;
import com.example.linkcargo.domain.chat.repository.ChatRepository;
import com.example.linkcargo.domain.chat.repository.ChatRoomRepository;
import com.example.linkcargo.domain.chat.repository.MemberShipRepository;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberShipRepository memberShipRepository;
    private final UserRepository userRepository;

    /**
     * 채팅방 생성 또는 조회
     */
    @Transactional
    public ChatRoom createOrGetChatRoom(Long userId, Long targetUserId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByUserIdAndTargetUserId(userId,
            targetUserId);

        // 채팅방이 존재하는 경우
        if (chatRoom.isPresent()) {
            return chatRoom.get(); // 채팅방 반환
        }

        // 채팅방이 존재하지 않는 경우 -> 새로 생성
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setTitle("Chat Room between " + userId + " and " + targetUserId); // 제목 설정
        newChatRoom.setStatus(RoomStatus.ENABLED); // 기본 상태 설정
        ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
        if (!isUserInChatRoom(userId, savedChatRoom.getId())) {
            addUserToChatRoom(userId, savedChatRoom.getId());
        }
        if (!isUserInChatRoom(targetUserId, savedChatRoom.getId())) {
            addUserToChatRoom(targetUserId, savedChatRoom.getId());
        }
        // 저장 후 반환
        return savedChatRoom;
    }

    /**
     * 채팅방의 채팅 목록 조회
     */
    public List<Chat> getChatsByRoomId(Long chatRoomId) {
        List<Chat> chats = chatRepository.findAllByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
        return chats;
    }

    /**
     * 유저의 모든 채팅방 조회
     */
    public List<ChatRoom> getChatRooms(Long userId) {
        return chatRoomRepository.findAllByUserId(userId);
    }

    /**
     * 채팅 저장
     */
    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    /**
     * 유저 채팅방 입장
     */
    public Membership addUserToChatRoom(Long userId, Long chatRoomId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        ChatRoom chatRoom = getChatRoom(chatRoomId);

        return memberShipRepository.save(new Membership(user, chatRoom));
    }

    /**
     * 유저가 채팅방에 이미 존재하는지 확인
     */
    public boolean isUserInChatRoom(Long userId, Long chatRoomId) {
        return memberShipRepository.existsByUserIdAndChatRoomId(userId, chatRoomId);
    }

    /**
     * 채팅방 조회
     */
    public ChatRoom getChatRoom(Long id) {
        return chatRoomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
    }

}
