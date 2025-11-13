package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO (sortie) pour l'objet embarqué Interests.
 */
public record InterestsDto(
        List<String> cuisines,
        List<String> techniques
) {
}