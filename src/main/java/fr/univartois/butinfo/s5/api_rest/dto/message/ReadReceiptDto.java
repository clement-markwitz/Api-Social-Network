package fr.univartois.butinfo.s5.api_rest.dto.message;

import java.time.LocalDateTime;

/**
 * DTO (sortie) pour l'objet embarqué ReadReceipt.
 */
public record ReadReceiptDto(
        String userId,
        LocalDateTime readAt
) {
}