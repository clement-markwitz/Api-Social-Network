package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.time.LocalDateTime;

/**
 * DTO (sortie) pour l'affichage des données complètes de l'utilisateur connecté.
 * Inclus les informations privées et les préférences.
 */
public record UserPrivateProfileDto(
        String id,
        String username,
        String email,
        String role,
        boolean banned,
        ProfileDto profile,
        PreferencesDto prefs,
        InterestsDto interests,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}