package fr.univartois.butinfo.s5.api_rest.dto.post;

/**
 * DTO (sortie) pour l'objet embarqué PostStats.
 */
public record PostStatsDto(
        int reactions,
        int comments
) {
}