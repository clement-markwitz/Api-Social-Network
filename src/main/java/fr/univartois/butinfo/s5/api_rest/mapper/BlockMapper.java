package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.block.BlockCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.block.BlockUserDto;
import fr.univartois.butinfo.s5.api_rest.model.Block;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BlockMapper {

    // --- Vers Entity (Création) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "blocker", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reason", source = "reason") // Explicite (optionnel si les noms sont identiques)
    Block toEntity(BlockCreateDto dto);

    // --- Vers DTO (Affichage) ---
    @Mapping(source = "blocked", target = "blockedUser")
    BlockUserDto toDto(Block entity);
}