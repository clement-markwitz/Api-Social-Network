package fr.univartois.butinfo.s5.api_rest.dto.reaction;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.ReactionType;

/**
 * DTO (sortie) pour afficher qui a réagi à un post.
 */
public record ReactionDto(
        String id,
        UserSummaryDto user, // Enrichi avec le résumé de l'utilisateur
        ReactionType type
) {
}