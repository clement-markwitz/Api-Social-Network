package fr.univartois.butinfo.s5.api_rest.dto.recommendation;

import lombok.Data;

@Data
public class FriendRecommendationDto {
    private String candidateName;
    private String candidateId;
    private Double totalScore;
}