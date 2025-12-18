package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageSummaryDto;

/**
 * DTO (output) for summarizing a conversation.
 * He is used in the conversation list of a user.
 */
public record ConversationSummaryDto(
        String id,
        String name, // Nom du groupe, ou nom du destinataire (si 1-to-1)
        MessageSummaryDto lastMessage // Dernier message de la conversation
) {
}