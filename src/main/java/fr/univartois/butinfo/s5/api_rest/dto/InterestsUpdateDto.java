package fr.univartois.butinfo.s5.api_rest.dto;

import java.util.List;

/**
 * DTO (entrée) pour la mise à jour manuelle des intérêts.
 */
public record InterestsUpdateDto(
        List<String> cuisines,
        List<String> techniques
) {
}