package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.recommendation.FriendRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.PageRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.PostRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PageRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
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

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final PageRepository pageRepository;

    private static final int RANDOM_FRIEND_RECOMMENDATION_COUNT = 5;

    private static final int RANDOM_PAGE_RECOMMENDATION_COUNT = 5;

    private static final int RANDOM_POST_RECOMMENDATION_COUNT = 10;

    @Value("${recommendation.api.url}")
    private String recommendationApiUrl;

    /**
     * Default constructor initializing the HTTP client.
     */
    public RecommendationService(UserRepository userRepository, PostRepository postRepository, PageRepository pageRepository) {
        this.restTemplate = new RestTemplate();
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pageRepository = pageRepository;
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
            logger.error("Error calling recommendation service (Friends): {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    /**
     * Retrieves a list of random friend recommendations for a specific user.
     *
     * @param userId The unique identifier of the user for whom recommendations are generated.
     * @return A list of {@link FriendRecommendationDto} containing suggested users.
     */
    public List<FriendRecommendationDto> getRandomFriendRecommendations(String userId) {

        List<User> randomUsers = userRepository.findRandomUsers(RANDOM_FRIEND_RECOMMENDATION_COUNT, userId);

        return randomUsers.stream().map(user -> {
            FriendRecommendationDto dto = new FriendRecommendationDto();
            dto.setCandidateId(user.getId());
            dto.setCandidateName(user.getProfile().getPseudo());
            // set a random score between 0.0 and 10.0
            dto.setTotalScore(Math.random() * 10.0);
            return dto;
        }).toList();

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
            logger.error("Error calling recommendation service (Pages): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a list of random page recommendations.
     *
     * @return A list of {@link PageRecommendationDto} containing suggested pages.
     */
    public List<PageRecommendationDto> getRandomPageRecommendations() {
        List<Page> randomPages = pageRepository.findRandomPages(RANDOM_PAGE_RECOMMENDATION_COUNT);

        return randomPages.stream().map(page -> {
            PageRecommendationDto dto = new PageRecommendationDto();
            dto.setId(page.getId());
            dto.setName(page.getName());
            dto.setTotalScore(Math.random() * 10.0);
            return dto;
        }).toList();
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
            logger.error("Error calling recommendation service (Posts): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a list of random post recommendations.
     *
     * @return A list of {@link PostRecommendationDto} containing suggested posts.
     */
    public List<PostRecommendationDto> getRandomPostRecommendations(String userId) {

        List<Post> randomPosts = postRepository.findRandomPosts(RANDOM_POST_RECOMMENDATION_COUNT, userId);

        return randomPosts.stream().map(post -> {
            PostRecommendationDto dto = new PostRecommendationDto();
            dto.setId(post.getId());
            dto.setLikedByFriends((long)( Math.random() * 20));
            return dto;
        }).toList();
    }
}