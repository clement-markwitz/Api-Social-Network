package fr.univartois.butinfo.s5.api_rest.dto.message;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (output) representing a message in a conversation.
 */
public record MessageDto(
        String id,
        String conversationId,
        UserSummaryDto sender, // The author of the message
        String text,
        List<String> attachments,
        LocalDateTime createdAt,
        List<ReadReceiptDto> readBy // List of users who have read the message
) {
}