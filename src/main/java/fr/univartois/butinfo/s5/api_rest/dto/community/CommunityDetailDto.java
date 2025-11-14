package fr.univartois.butinfo.s5.api_rest.dto.community;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (sortie) détaillé pour une Communauté.
 */
public record CommunityDetailDto(
        String id,
        String name,
        String description,
        String avatarUrl,
        List<UserSummaryDto> admins, // Enrichi
        int memberCount,
        List<String> topics,
        LocalDateTime createdAt
) {
}