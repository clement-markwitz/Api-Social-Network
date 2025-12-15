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

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "page", source = "pageId", qualifiedByName = "idToPage")
    @Mapping(target = "community", source = "communityId", qualifiedByName = "idToCommunity")
    Post toEntity(PostCreateDto dto);

    @Mapping(target = "pageId", source = "page.id")
    @Mapping(target = "communityId", source = "community.id")
    PostDto toDto(Post post);

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

    @Named("idToPage")
    default Page idToPage(String id) {
        if (id == null) return null;
        Page page = new Page();
        page.setId(id);
        return page;
    }

    @Named("idToCommunity")
    default Community idToCommunity(String id) {
        if (id == null) return null;
        Community community = new Community();
        community.setId(id);
        return community;
    }
}
