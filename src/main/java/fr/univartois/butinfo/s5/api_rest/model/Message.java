package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represent a message sent in a conversation between users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    /**
     * ID of the conversation this message belongs to.
     *  It's your "conversationId".
     */
    @Indexed
    @DBRef
    private Conversation conversation;

    /**
     * ID of the user who sent the message.
     * It's your "senderId".
     */
    @Indexed
    @DBRef
    private User sender;

    private String text;

    /**
     * List of attachment URLs.
     */
    private List<String> attachments;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * List of read receipts indicating which users have read the message.
     */
    @Indexed
    private List<ReadReceipt> readBy;
}
