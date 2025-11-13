// (Utilisé pour POST /api/register)
package fr.univartois.butinfo.s5.api_rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'enregistrement (création) d'un nouvel utilisateur.
 * C'est ce que le client envoie.
 */
public record UserCreateDto(
        @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
        @Size(min = 3, max = 20, message = "Le nom d'utilisateur doit contenir entre 3 et 20 caractères")
        String username,

        @NotBlank(message = "L'email ne peut pas être vide")
        @Email(message = "L'email doit être valide")
        String email,

        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        @Size(min = 8, max = 100, message = "Le mot de passe doit contenir au moins 8 caractères")
        String password,

        @NotBlank(message = "Le pseudo ne peut pas être vide")
        String pseudo
) {
}