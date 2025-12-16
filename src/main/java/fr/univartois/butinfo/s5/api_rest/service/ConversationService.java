package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.ConversationMapper;
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
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    public Conversation createConversation(ConversationCreateDto dto, String currentUserId) {
        Conversation conversation = conversationMapper.toEntity(dto);

        User initiator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur connecté introuvable"));
        conversation.setInitiator(initiator);

        List<String> memberIds = new ArrayList<>(dto.memberIds());
        if (!memberIds.contains(currentUserId)) {
            memberIds.add(currentUserId);
        }

        List<User> members = userRepository.findAllById(memberIds);
        conversation.setMembers(members);

        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

    public List<ConversationSummaryDto> getMyConversations(String currentUserId) {
        List<Conversation> conversations = conversationRepository.findByMembers_Id(currentUserId);

        return conversations.stream().map(conv -> {
            Message lastMsg = messageRepository.findFirstByConversation_IdOrderByCreatedAtDesc(conv.getId())
                    .orElse(null);
            return conversationMapper.toSummaryDto(conv, lastMsg, currentUserId);
        }).toList();
    }

    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable"));
    }
}