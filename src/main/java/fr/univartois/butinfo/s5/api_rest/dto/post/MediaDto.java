package fr.univartois.butinfo.s5.api_rest.dto.post;

import jakarta.validation.constraints.Size;

/**
 * DTO for media associated with a post.
 */
public record MediaDto(
        @Size(max = 2000000, message = "L'image est trop volumineuse (max ~1.5Mo)")
        String image,
        String videoUrl
) {
}