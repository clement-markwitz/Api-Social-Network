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

/**
 * Service responsible for communicating with the external recommendation API.
 * <p>
 * This service acts as an HTTP client to retrieve suggestions for friends,
 * pages, and posts personalized for a given user.
 * </p>
 * <p>
 * In case of unavailability of the remote service or technical errors,
 * the methods in this service are designed to fail silently by returning
 * empty lists, ensuring the main application is not blocked.
 * </p>
 */
@Service
public class RecommendationService {

    private final RestTemplate restTemplate;

    @Value("${recommendation.api.url}")
    private String recommendationApiUrl;

    /**
     * Default constructor initializing the HTTP client.
     */
    public RecommendationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Retrieves a list of friend recommendations for a specific user.
     *
     * @param userId The unique identifier of the user for whom recommendations are generated.
     * @return A list of {@link FriendRecommendationDto} containing suggested users.
     * Returns an empty list ({@code Collections.emptyList()}) if the API is unreachable or returns an error.
     */
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
            // Log error to standard error output (ideally should use an SLF4J Logger)
            System.err.println("Error calling recommendation service (Friends): " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a list of page recommendations (communities, businesses, etc.) for a user.
     *
     * @param userId The unique identifier of the user.
     * @return A list of {@link PageRecommendationDto} containing suggested pages.
     * Returns an empty list in case of a technical error.
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
            System.err.println("Error calling recommendation service (Pages): " + e.getMessage());
            return Collections.emptyList();
        }
    }


    /**
     * Retrieves a list of post recommendations likely to interest the user.
     *
     * @param userId The unique identifier of the user.
     * @return A list of {@link PostRecommendationDto} containing suggested posts.
     * Returns an empty list in case of a technical error.
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
            System.err.println("Error calling recommendation service (Posts): " + e.getMessage());
            return Collections.emptyList();
        }
    }
}