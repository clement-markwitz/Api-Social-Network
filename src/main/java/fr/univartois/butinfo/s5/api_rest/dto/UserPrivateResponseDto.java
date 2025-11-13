// (Réponse pour GET /api/me -> Profil privé de l'utilisateur connecté)
package fr.univartois.butinfo.s5.api_rest.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPrivateResponseDto {

    private String id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // DTOs imbriqués (complets)
    private ProfileDto profile;
    private InterestsDto interests;
    private PreferencesDto prefs; // MISE À JOUR : Inclus les préférences privées
}