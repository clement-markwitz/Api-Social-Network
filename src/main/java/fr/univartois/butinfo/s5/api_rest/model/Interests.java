package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model representing user interests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interests {
    private List<String> cuisines;
    private List<String> techniques;
}