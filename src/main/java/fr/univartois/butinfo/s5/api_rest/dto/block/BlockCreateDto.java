package fr.univartois.butinfo.s5.api_rest.dto.block;

/**
 * DTO (entrée) pour bloquer un utilisateur.
 */
public record BlockCreateDto(
        String reason
) {
}