package fr.univartois.butinfo.s5.api_rest.dto.page;

/**
 * DTO (sortie) résumé pour une Page (utilisé dans les listes).
 */
public record PageSummaryDto(
        String id,
        String name,
        String avatarUrl,
        int followerCount
) {
}