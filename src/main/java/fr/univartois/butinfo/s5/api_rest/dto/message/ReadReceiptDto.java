package fr.univartois.butinfo.s5.api_rest.dto.message;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;

/**
 * DTO (output) representing a read receipt for a message.
 */
public record ReadReceiptDto(
        UserSummaryDto user,
        LocalDateTime readAt
) {
}