package fr.univartois.butinfo.s5.api_rest.dto.post;

import fr.univartois.butinfo.s5.api_rest.dto.community.CommunitySummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.MediaDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostStatsDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.PostType;
import fr.univartois.butinfo.s5.api_rest.model.PostVisibility;
import java.time.LocalDateTime;

/**
 * DTO (sortie) principal pour un Post.
 * Entièrement enrichi pour l'affichage.
 */
public record PostDto(
        String id,
        UserSummaryDto author, // Enrichi
        String text,
        MediaDto media,
        PostStatsDto stats,
        PostType type,
        PostVisibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        // Le post peut être lié à une Page OU une Communauté
        String pageId,
        String communityId
) {
}