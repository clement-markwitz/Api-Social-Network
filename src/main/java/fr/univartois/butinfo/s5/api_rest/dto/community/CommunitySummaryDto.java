package fr.univartois.butinfo.s5.api_rest.dto.community;

/**
 * DTO (sortie) résumé pour une Communauté (utilisé dans les listes).
 */
public record CommunitySummaryDto(
        String id,
        String name,
        String avatarUrl,
        int memberCount
) {
}