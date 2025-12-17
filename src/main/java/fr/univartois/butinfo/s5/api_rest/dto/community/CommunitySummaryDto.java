package fr.univartois.butinfo.s5.api_rest.dto.community;

/**
 * DTO (output) for summarizing community information.
 */
public record CommunitySummaryDto(
        String id,
        String name,
        String avatarUrl,
        int memberCount
) {
}