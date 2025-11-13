package fr.univartois.butinfo.s5.api_rest.dto.comment;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;

import java.time.LocalDateTime;

/**
 * DTO (sortie) pour afficher un commentaire.
 */
public record CommentDto(
        String id,
        String postId,
        UserSummaryDto author, // Enrichi
        String text,
        String parentCommentId, // Gardé pour la logique de "tree" du front-end
        int likeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}