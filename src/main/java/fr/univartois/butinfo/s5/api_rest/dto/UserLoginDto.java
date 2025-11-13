package fr.univartois.butinfo.s5.api_rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour la connexion (login) d'un utilisateur.
 */
public record UserLoginDto(
        @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
        String username, // Ou email, selon votre logique de connexion

        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        String password
) {
}