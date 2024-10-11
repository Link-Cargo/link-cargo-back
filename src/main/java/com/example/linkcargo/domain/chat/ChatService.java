package com.example.linkcargo.domain.chat;

import com.example.linkcargo.domain.chat.Entity.Chat;
import com.example.linkcargo.domain.chat.Entity.ChatRoom;
import com.example.linkcargo.domain.chat.Entity.Membership;
import com.example.linkcargo.domain.chat.Entity.RoomStatus;
import com.example.linkcargo.domain.chat.dto.request.ChatRoomIdRequest;
import com.example.linkcargo.domain.chat.dto.response.ChatContentResponse;
import com.example.linkcargo.domain.chat.dto.response.ChatRoomResponse;
import com.example.linkcargo.domain.chat.repository.ChatRepository;
import com.example.linkcargo.domain.chat.repository.ChatRoomRepository;
import com.example.linkcargo.domain.chat.repository.MemberShipRepository;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    public ChatRoom createOrGetChatRoom(Long userId, ChatRoomIdRequest chatRoomIdRequest) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByUserIdAndTargetUserId(userId,
            chatRoomIdRequest.targetUserId());

        // 채팅방이 존재하는 경우
        if (chatRoom.isPresent()) {
            // 해당 채팅방의 스케줄 업데이트(동일할 수도 있고 다른 스케줄일 수도 있음)
            chatRoom.get().setSchedule(chatRoomIdRequest.schedule());
            return chatRoom.get(); // 채팅방 반환
        }

        // 채팅방이 존재하지 않는 경우 -> 새로 생성
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setTitle("Chat Room between " + userId + " and " + chatRoomIdRequest.targetUserId()); // 제목 설정
        newChatRoom.setSchedule(chatRoomIdRequest.schedule()); // 화주가 문의한 스케줄 정보 저장 -> 해당 채팅방 입장 시 보이게됨
        newChatRoom.setStatus(RoomStatus.ENABLED); // 기본 상태 설정
        newChatRoom.setMessageUpdatedAt(LocalDateTime.now()); // 가장 최근 메시지 도착 시간을 현재 시간으로 설정
        ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
        if (!isUserInChatRoom(userId, savedChatRoom.getId())) {
            addUserToChatRoom(userId, savedChatRoom.getId());
        }
        if (!isUserInChatRoom(chatRoomIdRequest.targetUserId(), savedChatRoom.getId())) {
            addUserToChatRoom(chatRoomIdRequest.targetUserId(), savedChatRoom.getId());
        }
        // 저장 후 반환
        return savedChatRoom;
    }

    /**
     * 채팅방의 채팅 목록 조회
     */
    public List<ChatContentResponse> getChatsByRoomId(Long chatRoomId) {
        List<Chat> chats = chatRepository.findAllByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        List<ChatContentResponse> chatContentResponses = chats.stream().map(
            chat -> new ChatContentResponse(
                chat.getId(),
                chat.getChatRoom().getId(),
                chat.getSender().getId(),
                chat.getMessageType(),
                chat.getContent(),
                chat.getFileName(),
                chat.getFileUrl(),
                chat.getCreatedAt()
            )
        ).toList();
        return chatContentResponses;
    }

    /**
     * 유저의 모든 채팅방 조회
     */
    public List<ChatRoomResponse> getChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUserIdOrderByMessageUpdatedAtDesc(userId);

        List<ChatRoomResponse> chatRoomResponses = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            List<Chat> latestChats = chatRepository.findLatestChatByChatRoomId(
                chatRoom.getId());

            String latestChatContent = "대화 기록이 없습니다."; // 기본값 설정
            if (!latestChats.isEmpty()) {
                // 최신 채팅의 첫 번째 항목을 가져옴
                Chat latestChat = latestChats.get(0);
                latestChatContent = latestChat.getContent();
            }

            List<Membership> memberships =
                memberShipRepository.findMembershipsByChatRoomIdAndExcludeUser(chatRoom.getId(),
                    userId);

            if (memberships.isEmpty()) {
                log.info("해당 채팅방의 참여 유저(membership) 이 존재하지 않습니다.");
                continue; // 회원이 없는 경우 처리, 필요에 따라 추가 로직 구현
            }

            User targetUser = memberships.get(0).getUser();

            // ChatRoomResponse 객체 생성
            ChatRoomResponse response = new ChatRoomResponse(
                chatRoom.getId(),
                targetUser.getId(),
                targetUser.getFirstName() + " " + targetUser.getLastName(),
                targetUser.getJobTitle(), // 포워더의 직책
                targetUser.getCompanyName(), // 포워더의 회사명
                chatRoom.getSchedule(), // 화주가 문의한 스케줄 정보
                chatRoom.getTitle(), // 채팅방 제목
                latestChatContent, // 가장 최근 대화 내역
                checkIsNew(chatRoom.getId(), userId), // 가장 최근 메시지가 상대방이 보낸 것이면서 읽지 않은 경우 TRUE
                chatRoom.getStatus()
            );

            chatRoomResponses.add(response);
        }

        return chatRoomResponses;
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

    /**
     * 채팅방의 상대방이 보낸 메시지 모두 읽음 처리
     * userId - 나의 ID
     */
    @Transactional
    public void makeAllChatRead(Long chatRoomId, Long userId) {
        List<Membership> memberships = memberShipRepository.findMembershipsByChatRoomIdAndExcludeUser(
            chatRoomId, userId);
        // 해당 채팅방의 상대방 조회
        User appositeUser = memberships.get(0).getUser();
        // 상대방이 보낸 모든 메시지 조회
        List<Chat> chatsByAppositeUser = chatRepository.findAllByChatRoomIdAndSenderId(
            chatRoomId, appositeUser.getId());
        // 상대방이 보낸 모든 메시지 읽음 처리
        chatsByAppositeUser.stream().forEach(chat -> chat.setIsRead(true));
    }

    /**
     * 특정 채팅방의 가장 최근 메시지가 상대방이 보낸 것이면서, 읽지 않은 경우 확인
     */
    public Boolean checkIsNew(Long chatRoomId, Long myId) {
        Optional<Chat> topChat = chatRepository.findTopByChatRoomIdOrderByIdDesc(chatRoomId);
        return topChat
            .filter(chat -> !chat.getSender().getId().equals(myId)) // 상대방이 보낸 메시지
            .filter(chat -> Boolean.FALSE.equals(chat.getIsRead())) // 읽지 않은 경우
            .isPresent(); // 조건을 모두 만족하면 true 반환
    }

    /**
     * (현재 채팅방일때 프론트엔드가 호출) 상대방이 보낸 특정 메시지 읽음 처리
     */
    @Transactional
    public void makeChatRead(Long chatRoomId, Long chatId, Long myId) {
        Chat chat = chatRepository.findById(chatId).get();
        // 내가 아니라, 상대방이 보낸 메시지인 경우 읽음 처리
        if(!chat.getSender().getId().equals(myId)){
            chat.setIsRead(true);
        }
    }

    /**
     * 채팅방 최근 메시지 시간 업데이트
     */
    @Transactional
    public void updateChatRoomMessageUpdatedTime(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
        chatRoom.updateMessageUpdatedAt();
    }
}
