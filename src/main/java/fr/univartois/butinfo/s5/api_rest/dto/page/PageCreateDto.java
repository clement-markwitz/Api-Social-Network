package fr.univartois.butinfo.s5.api_rest.dto.page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO (entrée) pour la création d'une nouvelle Page.
 */
public record PageCreateDto(
        @NotBlank(message = "Le nom ne peut pas être vide")
        @Size(min = 3, max = 100)
        String name,

        @Size(max = 500)
        String description,

        List<String> topics
) {
}