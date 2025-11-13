package fr.univartois.butinfo.s5.api_rest.dto.post;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO (entrée) pour la mise à jour d'un Post.
 * Limité aux champs modifiables.
 */
public record PostUpdateDto(
        @NotBlank
        String text
) {
}