package fr.univartois.butinfo.s5.api_rest.dto;

import java.util.List;

/**
 * DTO pour la mise à jour des préférences de l'utilisateur.
 */
public record PreferencesUpdateDto(
        List<String> diets,
        List<String> allergies,
        List<String> dislikedIngredients
) {
}