package fr.univartois.butinfo.s5.api_rest.dto.comment;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import java.time.LocalDateTime;

/**
 * DTO (output) representing a comment.
 */
public record CommentDto(
        String id,
        UserSummaryDto author,
        String text,
        String parentCommentId,
        int likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}