// (Utilisé pour la lecture ET la mise à jour des intérêts)
package fr.univartois.butinfo.s5.api_rest.dto;

import lombok.Data;
import java.util.List;

@Data
public class InterestsDto {
    private List<String> cuisines;
    private List<String> techniques;
}