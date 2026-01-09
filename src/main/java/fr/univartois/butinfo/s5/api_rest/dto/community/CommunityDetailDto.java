package fr.univartois.butinfo.s5.api_rest.dto.community;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (output) for detailed information about a community.
 */
public record CommunityDetailDto(
        String id,
        String name,
        String description,
        String avatarUrl,
        List<String> adminIds,
        int memberCount,
        List<String> topics,
        LocalDateTime createdAt,
        List<UserSummaryDto> members) {
}