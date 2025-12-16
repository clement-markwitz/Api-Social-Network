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
// J'ai supprimé l'import "java.util.stream.Collectors" qui ne sert plus

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    // --- Entrée (Input) ---

    Community toEntity(CommunityCreateDto dto);

    void updateEntityFromDto(CommunityUpdateDto dto, @MappingTarget Community entity);

    // --- Sortie (Output) ---

    CommunitySummaryDto toSummaryDto(Community entity);

    /**
     * Convertit une Entité en DTO détaillé.
     * Transformation explicite de List<User> (entité) vers List<String> (DTO).
     */
    @Mapping(target = "adminIds", source = "admins", qualifiedByName = "mapUsersToIds")
    CommunityDetailDto toDetailDto(Community entity);

    /**
     * Méthode helper utilisée par MapStruct pour extraire les IDs d'une liste d'utilisateurs.
     */
    @Named("mapUsersToIds")
    default List<String> mapUsersToIds(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        // CORRECTION SONAR : .toList() au lieu de .collect(Collectors.toList())
        return users.stream()
                .map(User::getId)
                .toList();
    }
}