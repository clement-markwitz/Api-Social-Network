package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.ban.BanCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Ban;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// On utilise UserMapper pour convertir User -> UserSummaryDto
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BanMapper {

    // --- Vers Entity (Création) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)      // Sera défini par le Controller/Service (ID dans l'URL)
    @Mapping(target = "moderator", ignore = true) // Sera défini par le Controller (Utilisateur connecté)
    @Mapping(target = "startAt", ignore = true)
    @Mapping(target = "endAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ban toEntity(BanCreateDto dto);

    // --- Vers DTO Complet (Affichage Détail) ---
    // MapStruct utilise automatiquement UserMapper.toSummaryDto pour convertir les champs User
    @Mapping(source = "user", target = "bannedUser")
    @Mapping(source = "moderator", target = "moderator")
    BanDto toDto(Ban ban);

    // --- Vers DTO Résumé (Liste) ---
    @Mapping(source = "user", target = "bannedUser")
    BanSummaryDto toSummaryDto(Ban ban);
}