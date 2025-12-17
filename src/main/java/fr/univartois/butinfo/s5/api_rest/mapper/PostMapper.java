package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper for converting between Post entities and their DTO representations.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    /**
     * Converts a PostCreateDto to a Post entity.
     *
     * @param dto the PostCreateDto to convert
     * @return the resulting Post entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "page", source = "pageId", qualifiedByName = "idToPage")
    @Mapping(target = "community", source = "communityId", qualifiedByName = "idToCommunity")
    Post toEntity(PostCreateDto dto);

    /**
     * Converts a Post entity to a PostDto.
     *
     * @param post the Post entity to convert
     * @return the resulting PostDto
     */
    @Mapping(target = "pageId", source = "page.id")
    @Mapping(target = "communityId", source = "community.id")
    PostDto toDto(Post post);

    /**
     * Updates an existing Post entity from a PostUpdateDto.
     *
     * @param dto the PostUpdateDto containing updated data
     * @param post the existing Post entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "visibility", ignore = true)
    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "page", ignore = true)
    @Mapping(target = "community", ignore = true)
    void updatePostFromDto(PostUpdateDto dto, @MappingTarget Post post);

    /**
     * Helper method to convert an ID string to a Page entity.
     *
     * @param id the ID of the Page
     * @return the Page entity with the given ID
     */
    @Named("idToPage")
    default Page idToPage(String id) {
        if (id == null) return null;
        Page page = new Page();
        page.setId(id);
        return page;
    }

    /**
     * Helper method to convert an ID string to a Community entity.
     *
     * @param id the ID of the Community
     * @return the Community entity with the given ID
     */
    @Named("idToCommunity")
    default Community idToCommunity(String id) {
        if (id == null) return null;
        Community community = new Community();
        community.setId(id);
        return community;
    }
}
