package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.block.BlockCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.block.BlockUserDto;
import fr.univartois.butinfo.s5.api_rest.model.Block;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Block entities and Block DTOs.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BlockMapper {

    // --- Vers Entity (Création) ---
    /**
     * Converts a BlockCreateDto to a Block entity.
     *
     * @param dto the BlockCreateDto to convert
     * @return the corresponding Block entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blocker", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reason", source = "reason") // Explicite (optionnel si les noms sont identiques)
    Block toEntity(BlockCreateDto dto);

    // --- Vers DTO (Affichage) ---
    /**
     * Converts a Block entity to a BlockUserDto.
     *
     * @param entity the Block entity to convert
     * @return the corresponding BlockUserDto
     */
    @Mapping(source = "blocked", target = "blockedUser")
    BlockUserDto toDto(Block entity);
}