package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO (output) representing user preferences.
 */
public record PreferencesDto(
        List<String> diets,
        List<String> allergies,
        List<String> dislikedIngredients
) {
}