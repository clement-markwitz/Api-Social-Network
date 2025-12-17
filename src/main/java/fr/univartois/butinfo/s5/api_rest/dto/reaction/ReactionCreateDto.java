package fr.univartois.butinfo.s5.api_rest.dto.reaction;

import fr.univartois.butinfo.s5.api_rest.model.ReactionType;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (entry point) for creating a new reaction.
 */
public record ReactionCreateDto(
        @NotNull(message = "Un type de réaction est requis")
        ReactionType type // L'utilisateur envoie le type de réaction (ex: LIKE)
) {
}