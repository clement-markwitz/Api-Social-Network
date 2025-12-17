package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO for the update of user preferences.
 */
public record PreferencesUpdateDto(
        List<String> diets,
        List<String> allergies,
        List<String> dislikedIngredients
) {
}