package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.mapper.MessageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.ConversationRepository;
import fr.univartois.butinfo.s5.api_rest.repository.MessageRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    private final SimpMessagingTemplate messagingTemplate;

    public MessageDto sendMessage(String conversationId, MessageCreateDto dto, String senderId) {
        // 1. Check conversation
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable"));

        // 2. Check membership
        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non membre");
        }

        // 3. Fetch sender object
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expéditeur introuvable"));

        // 4. Create Message
        Message message = messageMapper.toEntity(dto);
        message.setConversation(conversation);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setReadBy(new ArrayList<>());

        Message saved = messageRepository.save(message);
        // 5. Update Conversation timestamp
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        MessageDto messageDto = messageMapper.toDto(saved);
        //websocket
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, messageDto);
        return messageDto;
    }

    public List<MessageDto> getMessages(String conversationId, String currentUserId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));
        if (!isMember) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        List<Message> messages = messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId);
        return messageMapper.toDtoList(messages);
    }
    public void deleteMessage(String conversationId, String messageId, String currentUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable"));

        // Vérification de cohérence (le message appartient bien à la conversation)
        if (!message.getConversation().getId().equals(conversationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce message n'appartient pas à cette conversation");
        }

        // Vérification des droits (Seul l'auteur peut supprimer)
        if (!message.getSender().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez supprimer que vos propres messages");
        }

        messageRepository.delete(message);


        Map<String, String> deleteEvent = Map.of(
                "type", "DELETE_MESSAGE",
                "messageId", messageId
        );

        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, deleteEvent);
    }
}