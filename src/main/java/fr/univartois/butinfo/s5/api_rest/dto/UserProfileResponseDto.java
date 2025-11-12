// (Réponse pour GET /api/users/{id} -> Profil public)
package fr.univartois.butinfo.s5.api_rest.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileResponseDto {

    private String id;
    private LocalDateTime createdAt; // Pour "Membre depuis..."

    // DTOs imbriqués (publics)
    private ProfileDto profile;
    private InterestsDto interests;

    // Note : PreferencesDto (allergies, etc.) n'est PAS exposé publiquement.
}