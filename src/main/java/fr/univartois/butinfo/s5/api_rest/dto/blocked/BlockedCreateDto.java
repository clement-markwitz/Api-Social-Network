package fr.univartois.butinfo.s5.api_rest.dto.blocked;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO (entrée) pour bloquer un utilisateur.
 */
public record BlockedCreateDto(
        String reason
) {
}