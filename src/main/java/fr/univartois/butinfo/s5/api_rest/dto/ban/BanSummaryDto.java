package fr.univartois.butinfo.s5.api_rest.dto.ban;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

/**
 * DTO representing a summary of a ban.
 */
public record BanSummaryDto(
        String id,
        UserSummaryDto bannedUser,
        String reason,
        int durationDays,
        boolean active
) {
}
