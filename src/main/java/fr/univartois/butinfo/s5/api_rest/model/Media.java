package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Model representing media associated with a user or content.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    private String image;
    private String videoUrl;
}
