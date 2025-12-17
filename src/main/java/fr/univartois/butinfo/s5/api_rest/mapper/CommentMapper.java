package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentDto;
import fr.univartois.butinfo.s5.api_rest.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for Comment entities and DTOs.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    /**
     * Maps a CommentCreateDto to a Comment entity.
     * Ignores fields that are not set by the client.
     *
     * @param dto the CommentCreateDto
     * @return the Comment entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parentComment", source = "parentCommentId", qualifiedByName = "idToComment")
    Comment toEntity(CommentCreateDto dto);

    /**
     * Maps a Comment entity to a CommentDto.
     *
     * @param comment the Comment entity
     * @return the CommentDto
     */
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    CommentDto toDto(Comment comment);

    /**
     * Helper method to map an ID to a Comment entity with only the ID set.
     *
     * @param id the Comment ID
     * @return the Comment entity with only the ID set
     */
    @Named("idToComment")
    default Comment idToComment(String id) {
        if (id == null) return null;
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}