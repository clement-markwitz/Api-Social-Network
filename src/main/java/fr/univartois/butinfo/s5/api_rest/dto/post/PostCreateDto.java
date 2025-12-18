package fr.univartois.butinfo.s5.api_rest.dto.post;

import fr.univartois.butinfo.s5.api_rest.model.PostType;
import fr.univartois.butinfo.s5.api_rest.model.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (entry point) for creating a new post.
 */
public record PostCreateDto(
        @NotBlank
        String text,

        MediaDto media, // Le client envoie les URLs des médias uploadés

        @NotNull
        PostType type, // Le client doit spécifier le type

        @NotNull
        PostVisibility visibility, // Le client doit spécifier la visibilité

        // Le post peut être lié à une Page OU une Communauté, ou aucun (post personnel)
        String pageId,
        String communityId
) {
}