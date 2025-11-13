// (Réponse pour GET /api/admin/users -> Tableau de bord Admin)
package fr.univartois.butinfo.s5.api_rest.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserAdminResponseDto {
    private String id;
    private String username;
    private String email;
    private String role;
    private boolean banned; // L'admin peut voir le statut
    private LocalDateTime createdAt;
}