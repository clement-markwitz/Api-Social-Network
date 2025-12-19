package fr.univartois.butinfo.s5.api_rest.dto.ban;


import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;

/**
 * DTO (output) representing a user ban.
 */
public record BanDto(
        String id,
        UserSummaryDto bannedUser, // The user who has been banned
        UserSummaryDto moderator, // The moderator who issued the ban
        String reason,
        int durationDays,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean active,
        LocalDateTime createdAt
) {
}