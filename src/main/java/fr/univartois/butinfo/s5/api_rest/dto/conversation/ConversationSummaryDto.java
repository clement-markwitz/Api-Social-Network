package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageSummaryDto;

/**
 * DTO (output) for summarizing a conversation.
 * He is used in the conversation list of a user.
 */
public record ConversationSummaryDto(
        String id,
        String name, // The name of the conversation
        MessageSummaryDto lastMessage // The last message sent in the conversation
) {
}