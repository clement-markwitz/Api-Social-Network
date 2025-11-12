// (Utilisé pour POST /api/register)
package fr.univartois.butinfo.s5.api_rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequestDto {

    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank(message = "Le pseudo est requis")
    private String pseudo; // Pour pré-remplir le Profile
}