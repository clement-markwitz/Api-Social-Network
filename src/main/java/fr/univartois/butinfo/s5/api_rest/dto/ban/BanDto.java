package fr.univartois.butinfo.s5.api_rest.dto.ban;


import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.User;

import java.time.LocalDateTime;

/**
 * DTO (output) representing a user ban.
 */
public record BanDto(
        String id,
        UserSummaryDto bannedUser, // L'utilisateur qui EST banni
        UserSummaryDto moderator, // L'utilisateur qui A banni
        String reason,
        int durationDays,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean active,
        LocalDateTime createdAt
) {
}