package fr.univartois.butinfo.s5.api_rest.dto.post;

/**
 * DTO for media associated with a post.
 */
public record MediaDto(
        String imageUrl,
        String videoUrl
) {
}