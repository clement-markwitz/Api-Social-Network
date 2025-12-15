package fr.univartois.butinfo.s5.api_rest.dto.blocked;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;

/**
 * DTO (sortie) pour lister un utilisateur bloqué.
 */
public record BlockedUserDto(
        String id,
        String userId, // La personne qui est bloquée
        String reason,
        LocalDateTime createdAt
) {
}