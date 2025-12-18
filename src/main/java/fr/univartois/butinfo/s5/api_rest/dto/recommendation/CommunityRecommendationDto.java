package fr.univartois.butinfo.s5.api_rest.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommunityRecommendationDto {
    @JsonProperty("groupId")
    private String id;

    @JsonProperty("groupName")
    private String name;

    private Double totalScore;
}