package fr.univartois.butinfo.s5.api_rest.dto.ban;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DTO (entrée) pour la création d'un nouveau bannissement.
 */
public record BanCreateDto(
        @NotBlank
        String reason,

        @Positive(message = "La durée doit être un nombre positif de jours")
        int durationDays // ex: 30. Mettre 0 ou -1 pour permanent.
) {
}