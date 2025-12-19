package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.mapper.MessageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.ConversationRepository;
import fr.univartois.butinfo.s5.api_rest.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for managing message operations.
 * <p>
 * This service handles the persistence of messages in MongoDB and manages
 * real-time notifications via WebSockets (STOMP) when messages are sent or deleted.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final MessageMapper messageMapper;

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Retrieves a message by its unique identifier.
     *
     * @param id The ID of the message to retrieve.
     * @return The {@link Message} entity.
     * @throws ResponseStatusException if the message is not found (404).
     */
    public Message findById(String id) {
        return messageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Processes and sends a new message within a conversation.
     * <p>
     * This method performs several actions:
     * <ul>
     * <li>Links the message to the conversation and the sender.</li>
     * <li>Sets the creation timestamp.</li>
     * <li>Saves the message to the database.</li>
     * <li>Updates the conversation's {@code updatedAt} timestamp.</li>
     * <li><b>Real-time:</b> Broadcasts the new message to all subscribers of the conversation topic via WebSockets.</li>
     * </ul>
     * </p>
     *
     * @param conversation The conversation the message belongs to.
     * @param message      The message entity (prepared with content).
     * @param sender       The user sending the message.
     * @return The {@link MessageDto} of the saved message.
     */
    public MessageDto sendMessage(Conversation conversation, Message message, User sender) {

        message.setConversation(conversation);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setReadBy(new ArrayList<>());

        Message saved = messageRepository.save(message);

        // Update the conversation to reflect recent activity
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        MessageDto messageDto = messageMapper.toDto(saved);

        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), messageDto);

        return messageDto;
    }

    /**
     * Retrieves the complete history of messages for a specific conversation.
     *
     * @param conversation The conversation to retrieve messages from.
     * @return A list of {@link Message} entities ordered chronologically (oldest to newest).
     */
    public List<Message> getMessages(Conversation conversation) {
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversation.getId());
    }

    /**
     * Deletes a message and notifies clients in real-time.
     * <p>
     * Unlike a simple database deletion, this method sends a specific event payload
     * ({@code type: "DELETE_MESSAGE"}) via WebSockets. This allows frontend clients
     * to remove the message from the UI instantly without refreshing.
     * </p>
     *
     * @param conversationId The ID of the conversation (used for the WebSocket topic).
     * @param message        The message entity to delete.
     */
    public void deleteMessage(String conversationId, Message message) {

        messageRepository.delete(message);

        // Construct the deletion event payload
        Map<String, String> deleteEvent = Map.of(
                "type", "DELETE_MESSAGE",
                "messageId", message.getId()
        );

        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, deleteEvent);
    }
}