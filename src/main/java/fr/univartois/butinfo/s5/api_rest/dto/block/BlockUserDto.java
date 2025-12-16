package fr.univartois.butinfo.s5.api_rest.dto.block;

import java.time.LocalDateTime;

/**
 * DTO (sortie) pour lister un utilisateur bloqué.
 */
public record BlockUserDto(
        String id,
        String userId, // La personne qui est bloquée
        String reason,
        LocalDateTime createdAt
) {
}