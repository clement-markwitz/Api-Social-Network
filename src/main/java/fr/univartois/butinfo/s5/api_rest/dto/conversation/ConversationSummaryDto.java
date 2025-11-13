package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (sortie) pour afficher une conversation dans la liste principale.
 * Il est enrichi avec les détails des membres et le dernier message.
 */
public record ConversationSummaryDto(
        String id,
        String name, // Nom du groupe, ou nom du destinataire (si 1-to-1)
        List<UserSummaryDto> members, // Liste des membres
        MessageSummaryDto lastMessage, // Un résumé du dernier message
        LocalDateTime updatedAt // Pour trier la liste
) {
}