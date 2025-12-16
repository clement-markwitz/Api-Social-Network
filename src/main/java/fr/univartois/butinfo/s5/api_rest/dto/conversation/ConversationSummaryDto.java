package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageSummaryDto;

/**
 * DTO (sortie) pour afficher une conversation dans la liste principale.
 * Il est enrichi avec les détails des membres et le dernier message.
 */
public record ConversationSummaryDto(
        String id,
        String name, // Nom du groupe, ou nom du destinataire (si 1-to-1)
        MessageSummaryDto lastMessage // Dernier message de la conversation
) {
}