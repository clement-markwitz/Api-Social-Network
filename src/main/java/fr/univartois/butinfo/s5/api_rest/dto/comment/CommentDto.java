package fr.univartois.butinfo.s5.api_rest.dto.comment;


import java.time.LocalDateTime;

/**
 * DTO (output) representing a comment.
 */
public record CommentDto(
        String id,
        String postId,
        String authorId, // Enrichi
        String text,
        String parentCommentId,
        int likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}