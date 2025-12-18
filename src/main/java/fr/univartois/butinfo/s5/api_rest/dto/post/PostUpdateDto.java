package fr.univartois.butinfo.s5.api_rest.dto.post;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO (entry point) for updating an existing post.
 * Limit the update to the text content only.
 */
public record PostUpdateDto(
        @NotBlank
        String text
) {
}