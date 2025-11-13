package fr.univartois.butinfo.s5.api_rest.dto.page;

import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO (entrée) pour la mise à jour d'une Page existante.
 */
public record PageUpdateDto(
        @Size(max = 500)
        String description,

        List<String> topics
) {
}