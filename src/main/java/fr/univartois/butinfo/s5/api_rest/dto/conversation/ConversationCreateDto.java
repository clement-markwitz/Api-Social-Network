package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for creating a new conversation.
 */
public record ConversationCreateDto(
        @NotEmpty(message = "There must be at least one member in the conversation")
        List<String> memberIds,

        @Size(max = 100)
        String name
) {
}