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
/**
 * Mapper for converting between Page entities and their corresponding DTOs.
 */
@Mapper(componentModel = "spring", imports = {LocalDateTime.class, List.class, User.class, Collections.class})
public interface PageMapper {

    /**
     * Converts a Page entity to a PageDetailDto.
     *
     * @param page the Page entity to convert
     * @return the corresponding PageDetailDto
     */
    @Mapping(target = "adminIds", source = "admin", qualifiedByName = "mapUsersToIds")
    PageDetailDto toDetailDto(Page page);

    /**
     * Converts a Page entity to a PageSummaryDto.
     *
     * @param page the Page entity to convert
     * @return the corresponding PageSummaryDto
     */
    PageSummaryDto toSummaryDto(Page page);

    /**
     * Converts a PageCreateDto to a Page entity.
     *
     * @param dto the PageCreateDto to convert
     * @param creatorUserId the ID of the user creating the page
     * @return the corresponding Page entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", source = "creatorUserId", qualifiedByName = "mapCreatorToAdminList")
    @Mapping(target = "followerCount", constant = "0")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    Page toEntity(PageCreateDto dto, String creatorUserId);

    /**
     * Maps a list of User entities to a list of their IDs.
     *
     * @param admins the list of User entities
     * @return the list of User IDs
     */
    @Named("mapUsersToIds")
    default List<String> mapUsersToIds(List<User> admins) {
        if (admins == null) return Collections.emptyList();

        return admins.stream()
                .map(User::getId)
                .toList();
    }

    /**
     * Maps a creator user ID to a list containing a single User entity.
     *
     * @param creatorUserId the ID of the creator user
     * @return a list containing the User entity
     */
    @Named("mapCreatorToAdminList")
    default List<User> mapCreatorToAdminList(String creatorUserId) {
        if (creatorUserId == null) return Collections.emptyList();
        return List.of(User.builder().id(creatorUserId).build());
    }
}