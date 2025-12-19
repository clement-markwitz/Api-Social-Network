package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.repository.CommunityRepository;
import fr.univartois.butinfo.s5.api_rest.repository.MessageRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for retrieving statistics.
 */
@Service
public class StatsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final MessageRepository messageRepository;

    /**
     * Constructor for StatsService.
     *
     * @param userRepository The user repository
     * @param postRepository The post repository
     * @param communityRepository The community repository
     * @param messageRepository The message repository
     */
    public StatsService(UserRepository userRepository, PostRepository postRepository, CommunityRepository communityRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.messageRepository = messageRepository;
    }


    /**
     * Count users created between start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of users
     */
    public long countUsers(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return userRepository.countByCreatedAtBetween(start, end);
        }
        return userRepository.count();
    }

    /**
     * Count posts created between start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of posts
     */
    public long countPosts(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return postRepository.countByCreatedAtBetween(start, end);
        }
        return postRepository.count();
    }

    /**
     * Count communities created between start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of communities
     */
    public long countCommunities(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return communityRepository.countByCreatedAtBetween(start, end);
        }
        return communityRepository.count();
    }

    /**
     * Count messages created between start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of messages
     */
    public long countMessages(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return messageRepository.countByCreatedAtBetween(start, end);
        }
        return messageRepository.count();
    }
}