package fr.univartois.butinfo.s5.api_rest.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (entry point) for creating a new comment.
 */
public record CommentCreateDto(
        @NotBlank(message = "The comment text must not be blank")
        @Size(max = 2000)
        String text,

        String parentCommentId
) {
}