package fr.univartois.butinfo.s5.api_rest.dto.page;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (sortie) détaillé pour une Page.
 */
public record PageDetailDto(
        String id,
        String name,
        String description,
        String avatarUrl,
        List<String> adminIds, // Enrichi
        int followerCount,
        List<String> topics,
        LocalDateTime createdAt
) {
}