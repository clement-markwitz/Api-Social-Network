package fr.univartois.butinfo.s5.api_rest.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (entrée) pour la création d'un nouveau commentaire.
 */
public record CommentCreateDto(
        @NotBlank(message = "Le commentaire ne peut pas être vide")
        @Size(max = 2000)
        String text,

        String parentCommentId // Optionnel: ID du commentaire parent pour les réponses
) {
}