package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.page.PageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
// Import "java.util.stream.Collectors" supprimé car inutile avec .toList()

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, List.class, User.class, Collections.class})
public interface PageMapper {

    @Mapping(target = "adminIds", source = "admin", qualifiedByName = "mapUsersToIds")
    PageDetailDto toDetailDto(Page page);

    PageSummaryDto toSummaryDto(Page page);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", source = "creatorUserId", qualifiedByName = "mapCreatorToAdminList")
    @Mapping(target = "followerCount", constant = "0")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    Page toEntity(PageCreateDto dto, String creatorUserId);


    @Named("mapUsersToIds")
    default List<String> mapUsersToIds(List<User> admins) {
        if (admins == null) return Collections.emptyList();

        // CORRECTION SONAR : Utilisation de .toList() directement
        return admins.stream()
                .map(User::getId)
                .toList();
    }

    @Named("mapCreatorToAdminList")
    default List<User> mapCreatorToAdminList(String creatorUserId) {
        if (creatorUserId == null) return Collections.emptyList();
        return List.of(User.builder().id(creatorUserId).build());
    }
}