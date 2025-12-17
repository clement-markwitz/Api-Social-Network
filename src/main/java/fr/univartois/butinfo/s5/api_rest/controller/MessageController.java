package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.mapper.MessageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import fr.univartois.butinfo.s5.api_rest.service.ConversationService;
import fr.univartois.butinfo.s5.api_rest.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing messages within specific conversations.
 * <p>
 * This controller handles sending, retrieving, and deleting messages.
 * It ensures that only members of a conversation can interact with its messages.
 * </p>
 */
@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    /**
     * Utility method to retrieve the ID of the currently authenticated user.
     *
     * @param authentication The Spring Security authentication object containing the user's principal.
     * @return The unique ID of the authenticated user.
     * @throws ResponseStatusException if the user is not authenticated (401) or found in the database (401).
     */
    private String getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /**
     * Sends a new message to a specific conversation.
     * <p>
     * <b>Restriction:</b> The authenticated user must be a member of the conversation.
     * </p>
     *
     * @param conversationId The ID of the conversation where the message will be sent.
     * @param dto            The data transfer object containing the message content.
     * @param authentication The authentication object to identify the sender.
     * @return The created {@link MessageDto} representing the sent message.
     * @throws ResponseStatusException if the user is not a member of the conversation (403) or if the conversation/sender is not found (404).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto send(
            @PathVariable String conversationId,
            @RequestBody @Valid MessageCreateDto dto,
            Authentication authentication) {
        String senderId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(conversationId);

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this conversation");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        Message message = messageMapper.toEntity(dto);
        return messageService.sendMessage(conversation, message, sender);
    }

    /**
     * Retrieves all messages from a specific conversation.
     * <p>
     * <b>Restriction:</b> The authenticated user must be a member of the conversation to view its history.
     * </p>
     *
     * @param conversationId The ID of the conversation to retrieve messages from.
     * @param authentication The authentication object to verify membership.
     * @return A list of {@link MessageDto} representing the conversation history.
     * @throws ResponseStatusException if the user is not a member of the conversation (403).
     */
    @GetMapping
    public List<MessageDto> getAll(
            @PathVariable String conversationId,
            Authentication authentication) {
        String senderId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(conversationId);

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isMember) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return messageMapper.toDtoList(messageService.getMessages(conversation));
    }

    /**
     * Deletes a specific message from a conversation.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     * <li>The message must belong to the specified conversation.</li>
     * <li>Only the author of the message can delete it.</li>
     * </ul>
     * </p>
     *
     * @param conversationId The ID of the conversation containing the message.
     * @param messageId      The ID of the message to delete.
     * @param authentication The authentication object to verify ownership.
     * @throws ResponseStatusException if the message does not belong to the conversation (400) or if the user is not the author (403).
     */
    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);

        Message message = messageService.findById(messageId);

        if (!message.getConversation().getId().equals(conversationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This message does not belong to this conversation");
        }

        if (!message.getSender().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own messages");
        }

        messageService.deleteMessage(conversationId, message);
    }
}