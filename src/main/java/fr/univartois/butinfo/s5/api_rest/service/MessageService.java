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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    public MessageDto sendMessage(String conversationId, MessageCreateDto dto, String senderId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable"));

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non membre");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expéditeur introuvable"));

        Message message = messageMapper.toEntity(dto);
        message.setConversation(conversation);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setReadBy(new ArrayList<>());

        Message saved = messageRepository.save(message);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return messageMapper.toDto(saved);
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
}