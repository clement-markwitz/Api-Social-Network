package fr.univartois.butinfo.s5.api_rest.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for the user login process.
 */
public record UserLoginDto(
        @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
        String username,

        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        String password
) {
}