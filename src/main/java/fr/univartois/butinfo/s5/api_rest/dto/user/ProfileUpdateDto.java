package fr.univartois.butinfo.s5.api_rest.dto.user;

import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for updating user profile information.
 */
public record ProfileUpdateDto(
        @Size(min = 3, max = 30)
        String pseudo,

        @Size(max = 300, message = "La biographie ne doit pas dépasser 300 caractères")
        String bio,

        @Size(max = 100)
        String location,

        String avatarUrl,

        List<String> languages
) {
}