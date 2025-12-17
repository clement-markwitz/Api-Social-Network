package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

/**
 * Represent a read receipt for a message in a conversation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceipt {

    /**
     * ID of the user who has read the message.
     */
    @Indexed
    @DBRef
    private User user;

    /**
     * Timestamp when the message was read.
     */
    private LocalDateTime readAt;
}