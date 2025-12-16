package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.block.BlockCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.block.BlockUserDto;
import fr.univartois.butinfo.s5.api_rest.model.Block;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlockMapper {

    // --- Vers Entity (Création) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blocker", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Block toEntity(BlockCreateDto dto);

    // --- Vers DTO (Affichage) ---
    @Mapping(source = "blocked.id", target = "userId")
    BlockUserDto toDto(Block entity);
}