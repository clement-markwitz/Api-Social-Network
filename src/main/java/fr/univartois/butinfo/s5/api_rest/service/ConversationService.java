package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.ConversationMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.ConversationRepository;
import fr.univartois.butinfo.s5.api_rest.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class responsible for managing conversation persistence and state changes.
 *
 * This service focuses on database operations. Security checks and entity retrieval
 * are delegated to the controller where possible.
 *
 */
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationMapper conversationMapper;

    /**
     * Retrieves a conversation by its unique identifier.
     *
     * @param id The ID of the conversation to retrieve.
     * @return The {@link Conversation} entity.
     * @throws ResponseStatusException if the conversation is not found (404).
     */
    public Conversation findById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
    }

    /**
     * Persists a new conversation with a specific list of members.
     *
     * @param conversation The conversation entity to save.
     * @param members      The list of initial members.
     * @return The saved {@link Conversation} entity.
     */
    public Conversation createConversation(Conversation conversation, List<User> members) {
        conversation.setMembers(members);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    /**
     * Retrieves conversation summaries for a specific user.
     *
     * @param currentUserId The ID of the user.
     * @return A list of summaries including the last message.
     */
    public List<ConversationSummaryDto> getMyConversations(String currentUserId) {
        List<Conversation> conversations = conversationRepository.findByMembers_Id(currentUserId);

        return conversations.stream().map(conv -> {
            Message lastMsg = messageRepository.findFirstByConversation_IdOrderByCreatedAtDesc(conv.getId())
                    .orElse(null);
            return conversationMapper.toSummaryDto(conv, lastMsg, currentUserId);
        }).toList();
    }

    /**
     * Adds a user to the conversation member list and saves the changes.
     *
     * @param conversation The conversation entity.
     * @param user         The user entity to add.
     */
    public void joinConversation(Conversation conversation, User user) {
        conversation.getMembers().add(user);
        conversationRepository.save(conversation);
    }

    /**
     * Removes a user from the conversation.
     *
     * If the conversation becomes empty, it is deleted along with its messages.
     *
     *
     * @param conversation The conversation entity.
     * @param userId       The ID of the user to remove.
     */
    public void leaveConversation(Conversation conversation, String userId) {
        conversation.getMembers().removeIf(u -> u.getId().equals(userId));

        if (conversation.getMembers().isEmpty()) {
            List<Message> messages = messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversation.getId());
            messageRepository.deleteAll(messages);
            conversationRepository.delete(conversation);
        } else {
            conversationRepository.save(conversation);
        }
    }

    /**
     * Adds a list of users to the conversation, avoiding duplicates.
     *
     * @param conversation The conversation entity.
     * @param newUsers     The list of users to add.
     */
    public void addMembers(Conversation conversation, List<User> newUsers) {
        boolean updated = false;
        for (User userToAdd : newUsers) {
            boolean alreadyIn = conversation.getMembers().stream()
                    .anyMatch(member -> member.getId().equals(userToAdd.getId()));

            if (!alreadyIn) {
                conversation.getMembers().add(userToAdd);
                updated = true;
            }
        }

        if (updated) {
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }

    /**
     * Removes a specific member from the conversation.
     *
     * @param conversation The conversation entity.
     * @param memberId     The ID of the member to remove.
     */
    public void kickMember(Conversation conversation, String memberId) {
        boolean removed = conversation.getMembers().removeIf(u -> u.getId().equals(memberId));

        if (removed) {
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }
}