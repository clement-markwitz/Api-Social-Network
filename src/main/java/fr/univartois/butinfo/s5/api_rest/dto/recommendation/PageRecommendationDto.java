package fr.univartois.butinfo.s5.api_rest.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageRecommendationDto {
    @JsonProperty("pageId")
    private String id;

    @JsonProperty("pageName")
    private String name;

    private Double totalScore;
}