package fr.univartois.butinfo.s5.api_rest.dto.community;

import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO (entrée) pour la mise à jour d'une Communauté existante.
 */
public record CommunityUpdateDto(

        String name,

        @Size(max = 500)
        String description,

        String avatarUrl,

        List<String> topics
) {
}