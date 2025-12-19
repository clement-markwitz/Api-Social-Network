package fr.univartois.butinfo.s5.api_rest.dto.post;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.PostType;
import fr.univartois.butinfo.s5.api_rest.model.PostVisibility;
import java.time.LocalDateTime;

/**
 * DTO (output) representing a post with enriched author information.
 */
public record PostDto(
        String id,
        UserSummaryDto author,
        String text,
        MediaDto media,
        PostStatsDto stats,
        PostType type,
        PostVisibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        // The post can be associated with either a page or a community
        String pageId,
        String communityId
) {
}