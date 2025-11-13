package fr.univartois.butinfo.s5.api_rest.dto.message;

import fr.univartois.butinfo.s5.api_rest.dto.message.ReadReceiptDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (sortie) pour afficher un message complet dans un chat.
 */
public record MessageDto(
        String id,
        String conversationId,
        UserSummaryDto sender, // Auteur du message (enrichi)
        String text,
        List<String> attachments,
        LocalDateTime createdAt,
        List<ReadReceiptDto> readBy // Liste de qui l'a lu
) {
}