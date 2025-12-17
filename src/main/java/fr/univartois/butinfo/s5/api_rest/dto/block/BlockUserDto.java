package fr.univartois.butinfo.s5.api_rest.dto.block;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;

/**
 * DTO (sortie) pour lister un utilisateur bloqué.
 */
public record BlockUserDto(
        String id,
        UserSummaryDto blockedUser, // La personne qui est bloquée
        String reason,
        LocalDateTime createdAt
) {
}