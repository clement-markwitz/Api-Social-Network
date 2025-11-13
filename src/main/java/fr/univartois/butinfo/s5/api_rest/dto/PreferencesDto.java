// (Utilisé pour la lecture ET la mise à jour des préférences)

package fr.univartois.butinfo.s5.api_rest.dto;

import lombok.Data;
import java.util.List;

@Data
public class PreferencesDto {
    private List<String> diets;
    private List<String> allergies;
    private List<String> dislikedIngredients;
}