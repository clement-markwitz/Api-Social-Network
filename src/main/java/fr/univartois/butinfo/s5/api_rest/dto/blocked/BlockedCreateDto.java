package fr.univartois.butinfo.s5.api_rest.dto.blocked;


/**
 * DTO (entrée) pour bloquer un utilisateur.
 */
public record BlockedCreateDto(
        String reason
) {
}