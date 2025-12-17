package fr.univartois.butinfo.s5.api_rest.dto.page;

/**
 * DTO (output) for summarizing page information.
 */
public record PageSummaryDto(
        String id,
        String name,
        String avatarUrl,
        int followerCount
) {
}