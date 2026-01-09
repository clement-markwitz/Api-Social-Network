package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunitySummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

/**
 * Mapper pour les communautés.
 * Utilise MapStruct pour la conversion entre entités et DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        UserMapper.class })
public interface CommunityMapper {

    /**
     * Convertit un DTO de création en Entité.
     */
    Community toEntity(CommunityCreateDto dto);

    /**
     * Met à jour une Entité existante à partir d'un DTO de mise à jour.
     */
    void updateEntityFromDto(CommunityUpdateDto dto, @MappingTarget Community entity);

    /**
     * Convertit une Entité en DTO résumé.
     */
    CommunitySummaryDto toSummaryDto(Community entity);

    /**
     * Converts a Community entity to a CommunityDetailDto with members.
     * Note: members must be provided separately as they come from
     * CommunityMembership.
     */
    @Mapping(target = "adminIds", source = "entity.admins", qualifiedByName = "mapUsersToIds")
    @Mapping(target = "members", source = "members")
    CommunityDetailDto toDetailDto(Community entity, List<User> members);

    /**
     * Method helper to map a list of User entities to a list of their IDs.
     */
    @Named("mapUsersToIds")
    default List<String> mapUsersToIds(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }

    /**
     * Convert a list of Users to a list of UserSummaryDto.
     */
    List<UserSummaryDto> toUserSummaryDtoList(List<User> users);
}