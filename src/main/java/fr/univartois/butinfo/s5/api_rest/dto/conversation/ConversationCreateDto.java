package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO (entrée) pour la création d'une nouvelle conversation.
 */
public record ConversationCreateDto(
        @NotEmpty(message = "Il doit y avoir au moins un autre membre")
        List<String> memberIds,

        @Size(max = 100)
        String name
) {
}