package fr.univartois.butinfo.s5.api_rest.dto.message;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO (entrée) pour envoyer un nouveau message dans une conversation.
 */
public record MessageCreateDto(
        @NotBlank
        String text,

        List<String> attachments // Optionnel: liste d'URLs d'images/fichiers
) {
}