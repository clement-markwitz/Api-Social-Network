package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.user.*;
import fr.univartois.butinfo.s5.api_rest.model.Interests;
import fr.univartois.butinfo.s5.api_rest.model.Preferences;
import fr.univartois.butinfo.s5.api_rest.model.Profile;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

    PreferencesDto toPreferencesDto(Preferences preferences);

    InterestsDto toInterestsDto(Interests interests);

    void updateProfileFromDto(ProfileUpdateDto dto, @MappingTarget Profile profile);

    void updatePreferencesFromDto(PreferencesUpdateDto dto, @MappingTarget Preferences preferences);

    void updateInterestsFromDto(InterestsUpdateDto dto, @MappingTarget Interests interests);
}