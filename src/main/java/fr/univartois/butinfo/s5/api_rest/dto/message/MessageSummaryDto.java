package fr.univartois.butinfo.s5.api_rest.dto.message;

import java.time.LocalDateTime;

/**
 * DTO representing a summary of a message for listing purposes.
 */
public record MessageSummaryDto(
        String text,
        LocalDateTime createdAt,
        boolean isRead
) {
}