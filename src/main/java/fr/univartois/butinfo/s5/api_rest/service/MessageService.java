package fr.univartois.butinfo.s5.api_rest.service;

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
    private final MessageMapper messageMapper;

    private final SimpMessagingTemplate messagingTemplate;

    public Message findById(String id) {
        return messageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    public MessageDto sendMessage(Conversation conversation, Message message, User sender) {

        message.setConversation(conversation);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setReadBy(new ArrayList<>());

        Message saved = messageRepository.save(message);
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        MessageDto messageDto = messageMapper.toDto(saved);
        //websocket
        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), messageDto);
        return messageDto;
    }

    public List<Message> getMessages(Conversation conversation) {

        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversation.getId());

    }
    public void deleteMessage(String conversationId, Message message) {

        messageRepository.delete(message);

        Map<String, String> deleteEvent = Map.of(
                "type", "DELETE_MESSAGE",
                "messageId", message.getId()
        );

        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, deleteEvent);
    }
}