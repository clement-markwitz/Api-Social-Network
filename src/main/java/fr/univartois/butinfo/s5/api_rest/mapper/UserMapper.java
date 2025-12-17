package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.user.*;
import fr.univartois.butinfo.s5.api_rest.model.Interests;
import fr.univartois.butinfo.s5.api_rest.model.Preferences;
import fr.univartois.butinfo.s5.api_rest.model.Profile;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between User entities and various User DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a UserCreateDto to a User entity.
     *
     * @param dto the UserCreateDto to convert
     * @return the resulting User entity
     */
    @Mapping(target = "profile.pseudo", source = "pseudo")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "banned", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "prefs", ignore = true)
    @Mapping(target = "interests", ignore = true)
    User toEntity(UserCreateDto dto);

    /**
     * Converts a User entity to a UserPrivateProfileDto.
     *
     * @param user the User entity to convert
     * @return the resulting UserPrivateProfileDto
     */
    UserPrivateProfileDto toPrivateProfileDto(User user);

    /**
     * Converts a User entity to a UserPublicProfileDto.
     *
     * @param user the User entity to convert
     * @return the resulting UserPublicProfileDto
     */
    @Mapping(target = "pseudo", source = "profile.pseudo")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    @Mapping(target = "location", source = "profile.location")
    @Mapping(target = "languages", source = "profile.languages")
    UserPublicProfileDto toPublicProfileDto(User user);

    /**
     * Converts a User entity to a UserSummaryDto.
     *
     * @param user the User entity to convert
     * @return the resulting UserSummaryDto
     */
    @Mapping(target = "pseudo", source = "profile.pseudo")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    UserSummaryDto toSummaryDto(User user);

    /**
     * Converts a Profile entity to a ProfileDto.
     * @param preferences
     * @return
     */
    PreferencesDto toPreferencesDto(Preferences preferences);

    /**
     * Converts an Interests entity to an InterestsDto.
     * @param interests
     * @return
     */
    InterestsDto toInterestsDto(Interests interests);

    /**
     * Updates an existing Profile entity from a ProfileUpdateDto.
     *
     * @param dto the ProfileUpdateDto containing updated data
     * @param profile the existing Profile entity to update
     */
    void updateProfileFromDto(ProfileUpdateDto dto, @MappingTarget Profile profile);

    /**
     * Updates an existing Preferences entity from a PreferencesUpdateDto.
     *
     * @param dto the PreferencesUpdateDto containing updated data
     * @param preferences the existing Preferences entity to update
     */
    void updatePreferencesFromDto(PreferencesUpdateDto dto, @MappingTarget Preferences preferences);

    /**
     * Updates an existing Interests entity from an InterestsUpdateDto.
     *
     * @param dto the InterestsUpdateDto containing updated data
     * @param interests the existing Interests entity to update
     */
    void updateInterestsFromDto(InterestsUpdateDto dto, @MappingTarget Interests interests);
}