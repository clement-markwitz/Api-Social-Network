package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentDto;
import fr.univartois.butinfo.s5.api_rest.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    // --- Vers Entity (Création) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parentComment", source = "parentCommentId", qualifiedByName = "idToComment")
    Comment toEntity(CommentCreateDto dto);

    @Mapping(target = "parentCommentId", source = "parentComment.id")
    CommentDto toDto(Comment comment);

    @Named("idToComment")
    default Comment idToComment(String id) {
        if (id == null) return null;
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}