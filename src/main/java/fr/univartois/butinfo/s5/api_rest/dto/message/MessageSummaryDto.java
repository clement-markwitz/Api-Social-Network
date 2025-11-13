package fr.univartois.butinfo.s5.api_rest.dto.message;

import java.time.LocalDateTime;

/**
 * DTO (sortie) minimal pour le *dernier message* affiché dans la liste
 * des conversations.
 */
public record MessageSummaryDto(
        String text,
        LocalDateTime createdAt
) {
}