package fr.univartois.butinfo.s5.api_rest.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (entry point) for updating an existing comment.
 */
public record CommentUpdateDto(
        @NotBlank(message = "The comment text must not be blank")
        @Size(max = 2000)
        String text
) {
}