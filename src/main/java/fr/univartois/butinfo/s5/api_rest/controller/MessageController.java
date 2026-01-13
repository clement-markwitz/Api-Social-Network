package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.mapper.MessageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.ConversationService;
import fr.univartois.butinfo.s5.api_rest.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final MessageMapper messageMapper;

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
    @Operation(summary = "Send a message", description = "Sends a new message in the specified conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message sent successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Not a member of this conversation)"),
            @ApiResponse(responseCode = "404", description = "Conversation or sender not found")
    })
    public MessageDto send(
            @PathVariable String conversationId,
            @RequestBody @Valid MessageCreateDto dto,
            Authentication authentication) {

        User sender = (User) authentication.getPrincipal();
        Conversation conversation = conversationService.findById(conversationId);

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
    @Operation(summary = "Get messages", description = "Retrieves all messages of a specified conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (You are not a member of this conversation)")
    })
    public List<MessageDto> getAll(
            @PathVariable String conversationId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Conversation conversation = conversationService.findById(conversationId);

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
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
    @Operation(summary = "Delete a message", description = "Deletes a specific message from a conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "400", description = "The message does not belong to this conversation"),
            @ApiResponse(responseCode = "403", description = "Access denied (You can only delete your own messages)"),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    public void delete(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Message message = messageService.findById(messageId);

        if (!message.getConversation().getId().equals(conversationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This message does not belong to this conversation");
        }

        if (!message.getSender().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own messages");
        }

        messageService.deleteMessage(conversationId, message);
    }
}