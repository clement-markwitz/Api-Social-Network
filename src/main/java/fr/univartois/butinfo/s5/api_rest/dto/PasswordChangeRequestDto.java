// (Utilisé pour POST /api/me/change-password)
package fr.univartois.butinfo.s5.api_rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (entrée) pour la demande de changement de mot de passe.
 */
public record PasswordChangeRequestDto(
    @NotBlank
    String oldPassword,

    @NotBlank
    @Size(min = 8, max = 100)
    String newPassword
) {}