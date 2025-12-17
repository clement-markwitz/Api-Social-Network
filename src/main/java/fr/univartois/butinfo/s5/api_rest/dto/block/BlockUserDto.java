package fr.univartois.butinfo.s5.api_rest.dto.block;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;

/**
 * DTO (output) for representing a blocked user.
 */
public record BlockUserDto(
        String id,
        UserSummaryDto blockedUser, // La personne qui est bloquée
        String reason,
        LocalDateTime createdAt
) {
}