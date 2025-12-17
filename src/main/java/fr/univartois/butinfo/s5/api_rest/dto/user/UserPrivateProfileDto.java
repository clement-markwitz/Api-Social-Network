package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.time.LocalDateTime;

/**
 * DTO (output) représentant le profil privé d'un utilisateur.
 * Include information that should only be visible to the user themselves.
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