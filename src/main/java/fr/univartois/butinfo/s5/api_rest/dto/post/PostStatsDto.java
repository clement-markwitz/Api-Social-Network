package fr.univartois.butinfo.s5.api_rest.dto.post;

/**
 * DTO (output) for post statistics.
 */
public record PostStatsDto(
        int reactions,
        int comments
) {
}