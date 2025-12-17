package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting between Reaction entities and their DTO representations.
 */
@Mapper(componentModel = "spring")
public interface ReactionMapper {

    /**
     * Converts a ReactionCreateDto to a Reaction entity.
     * Ignores fields that are managed by the system (id, post, user, createdAt).
     *
     * @param dto the ReactionCreateDto to convert
     * @return the corresponding Reaction entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reaction toEntity(ReactionCreateDto dto);

    /**
     * Converts a Reaction entity to a ReactionDto.
     * Maps the user field to a UserSummaryDto using a qualified method.
     *
     * @param reaction the Reaction entity to convert
     * @return the corresponding ReactionDto
     */
    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserToSummary")
    ReactionDto toDto(Reaction reaction);

    /**
     * Maps a User entity to a UserSummaryDto.
     * This is a custom mapping method used in the Reaction to ReactionDto conversion.
     *
     * @param user the User entity to map
     * @return the corresponding UserSummaryDto
     */
    @Named("mapUserToSummary")
    default UserSummaryDto mapUserToSummary(User user) {
        if (user == null) return null;
        return new UserSummaryDto(
                user.getId(),
                "Utilisateur " + user.getId(),
                "https://ui-avatars.com/api/?name=" + user.getId()
        );
    }
}
