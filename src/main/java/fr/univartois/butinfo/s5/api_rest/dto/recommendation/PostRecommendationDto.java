package fr.univartois.butinfo.s5.api_rest.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRecommendationDto {
    @JsonProperty("postId")
    private String id;

    private Long likedByFriends;
}