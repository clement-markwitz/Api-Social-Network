package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO (entry point) for updating user interests.
 */
public record InterestsUpdateDto(
        List<String> cuisines,
        List<String> techniques
) {
}