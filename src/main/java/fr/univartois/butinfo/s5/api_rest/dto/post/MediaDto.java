package fr.univartois.butinfo.s5.api_rest.dto.post;

/**
 * DTO (sortie) pour l'objet embarqué Media.
 */
public record MediaDto(
        String imageUrl,
        String videoUrl
) {
}