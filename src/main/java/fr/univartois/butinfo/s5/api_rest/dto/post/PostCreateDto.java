package fr.univartois.butinfo.s5.api_rest.dto.post;

import fr.univartois.butinfo.s5.api_rest.model.PostType;
import fr.univartois.butinfo.s5.api_rest.model.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (entry point) for creating a new post.
 */
public record PostCreateDto(
        @NotBlank
        String text,

        MediaDto media, // The client can optionally provide media

        @NotNull
        PostType type, // The client must specify the post type

        @NotNull
        PostVisibility visibility, // The client must specify the post visibility

        // The post can be associated with either a page or a community
        String pageId,
        String communityId
) {
}