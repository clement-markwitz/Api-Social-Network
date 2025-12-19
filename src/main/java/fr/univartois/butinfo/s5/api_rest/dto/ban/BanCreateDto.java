package fr.univartois.butinfo.s5.api_rest.dto.ban;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 *  DTO for creating a new ban.
 */
public record BanCreateDto(
        @NotBlank
        String reason,

        @Positive(message = "The duration must be a positive number of days")
        int durationDays // ex : 7 for a week
) {
}