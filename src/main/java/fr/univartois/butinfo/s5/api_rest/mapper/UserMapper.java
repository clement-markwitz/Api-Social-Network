package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPrivateProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPublicProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Profile;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "profile.pseudo", source = "pseudo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "banned", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prefs", ignore = true)
    @Mapping(target = "interests", ignore = true)
    User toEntity(UserCreateDto dto);

    UserPrivateProfileDto toPrivateProfileDto(User user);

    @Mapping(target = "pseudo", source = "profile.pseudo")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    @Mapping(target = "location", source = "profile.location")
    @Mapping(target = "languages", source = "profile.languages")
    UserPublicProfileDto toPublicProfileDto(User user);

    @Mapping(target = "pseudo", source = "profile.pseudo")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    UserSummaryDto toSummaryDto(User user);
}