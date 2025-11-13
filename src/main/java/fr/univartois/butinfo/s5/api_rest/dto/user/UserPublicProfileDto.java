package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (sortie) pour l'affichage de la page de profil publique d'un utilisateur.
 * Note : J'aplatis les champs de ProfileDto ici pour un accès plus simple.
 */
public record UserPublicProfileDto(
        String id,
        String username,
        String pseudo,
        String bio,
        String avatarUrl,
        String location,
        List<String> languages,
        InterestsDto interests,
        LocalDateTime createdAt
) {
}