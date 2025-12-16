package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reaction toEntity(ReactionCreateDto dto);

    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserToSummary")
    ReactionDto toDto(Reaction reaction);

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
