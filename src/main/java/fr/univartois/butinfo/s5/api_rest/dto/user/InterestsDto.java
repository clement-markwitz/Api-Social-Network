package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO (output) representing a user's interests.
 */
public record InterestsDto(
        List<String> cuisines,
        List<String> techniques
) {
}