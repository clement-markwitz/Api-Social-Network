package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO (sortie) pour l'objet embarqué Preferences.
 */
public record PreferencesDto(
        List<String> diets,
        List<String> allergies,
        List<String> dislikedIngredients
) {
}