package fr.univartois.butinfo.s5.api_rest.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (entrée) pour la mise à jour d'un commentaire existant.
 */
public record CommentUpdateDto(
        @NotBlank(message = "Le commentaire ne peut pas être vide")
        @Size(max = 2000)
        String text
) {
}