package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunitySummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityUpdateDto;
import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

/**
 * Mapper pour les communautés.
 * Utilise MapStruct pour la conversion entre entités et DTOs.
 */
@Mapper(componentModel = "spring")
public interface CommunityMapper {

    // --- Entrée (Input) ---
    /**
     * Convertit un DTO de création en Entité.
     */
    Community toEntity(CommunityCreateDto dto);

    /**
     * Met à jour une Entité existante à partir d'un DTO de mise à jour.
     */
    void updateEntityFromDto(CommunityUpdateDto dto, @MappingTarget Community entity);

    // --- Sortie (Output) ---

    /**
     * Convertit une Entité en DTO résumé.
     */
    CommunitySummaryDto toSummaryDto(Community entity);

    /**
     * We need to explicitly map the list of admins (List<User>) to a list of their IDs (List<String>)
     * when converting to CommunityDetailDto.
     */
    @Mapping(target = "adminIds", source = "admins", qualifiedByName = "mapUsersToIds")
    CommunityDetailDto toDetailDto(Community entity);

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
}