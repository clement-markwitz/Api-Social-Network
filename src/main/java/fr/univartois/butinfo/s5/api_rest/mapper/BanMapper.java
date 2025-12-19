package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.ban.BanCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Ban;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Ban entities and their corresponding DTOs.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BanMapper {

    /**
     * Converts a BanCreateDto to a Ban entity.
     * Certain fields are ignored as they will be set by the controller/service.
     *
     * @param dto the BanCreateDto to convert
     * @return the resulting Ban entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "startAt", ignore = true)
    @Mapping(target = "endAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ban toEntity(BanCreateDto dto);

    /**
     * Converts a Ban entity to a BanDto.
     *
     * @param ban the Ban entity to convert
     * @return the resulting BanDto
     */
    @Mapping(source = "user", target = "bannedUser")
    @Mapping(source = "moderator", target = "moderator")
    BanDto toDto(Ban ban);

    /** MapStruct utilise automatiquement UserMapper.toSummaryDto pour convertir les champs User
     /**
     * Converts a Ban entity to a BanSummaryDto.
     *
     * @param ban the Ban entity to convert
     * @return the resulting BanSummaryDto
     */
    @Mapping(source = "user", target = "bannedUser")
    BanSummaryDto toSummaryDto(Ban ban);
}