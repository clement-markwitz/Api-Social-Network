package fr.univartois.butinfo.s5.api_rest.dto.ban;


import java.time.LocalDateTime;

/**
 * DTO (sortie) pour afficher les détails d'un bannissement.
 */
public record BanDto(
        String id,
        String userId, // L'utilisateur qui EST banni
        String moderatorId, // L'utilisateur qui A banni
        String reason,
        int durationDays,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean active,
        LocalDateTime createdAt
) {
}