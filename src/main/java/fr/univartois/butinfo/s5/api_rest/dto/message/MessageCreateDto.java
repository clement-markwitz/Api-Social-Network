package fr.univartois.butinfo.s5.api_rest.dto.message;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO for creating a new message.
 */
public record MessageCreateDto(
        @NotBlank
        String text,

        List<String> attachments // Optional list of attachment URLs
) {
}