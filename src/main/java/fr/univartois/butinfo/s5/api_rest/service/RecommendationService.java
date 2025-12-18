package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.recommendation.FriendRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.PageRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.PostRecommendationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;

    @Value("${recommendation.api.url}")
    private String recommendationApiUrl;

    public RecommendationService() {
        this.restTemplate = new RestTemplate();
    }

    public List<FriendRecommendationDto> getFriendRecommendations(String userId) {
        String url = recommendationApiUrl + "/recommendations/friends/" + userId;

        try {
            ResponseEntity<List<FriendRecommendationDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FriendRecommendationDto>>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel au service de recommandation : " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Récupère les recommandations de pages.
     */
    public List<PageRecommendationDto> getPageRecommendations(String userId) {
        String url = recommendationApiUrl + "/recommendations/pages/" + userId;
        try {
            ResponseEntity<List<PageRecommendationDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<PageRecommendationDto>>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erreur reco pages : " + e.getMessage());
            return Collections.emptyList();
        }
    }


    /**
     * Récupère les recommandations de posts.
     */
    public List<PostRecommendationDto> getPostRecommendations(String userId) {
        String url = recommendationApiUrl + "/recommendations/posts/" + userId;
        try {
            ResponseEntity<List<PostRecommendationDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<PostRecommendationDto>>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erreur reco posts : " + e.getMessage());
            return Collections.emptyList();
        }
    }
}