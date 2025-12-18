package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Community entities in MongoDB.
 */
public interface CommunityRepository extends MongoRepository<Community, String> {

    /**
     * Find a community by its name.
     *
     * @param name the name of the community
     * @return an Optional containing the found Community or empty if not found
     */
    Optional<Community> findByName(String name);

    /**
     * Check if a community exists by its name.
     *
     * @param name the name of the community
     * @return true if a community with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find communities with names containing the specified string, case insensitive.
     *
     * @param name the substring to search for in community names
     * @return a list of communities with names containing the specified string
     */
    List<Community> findByNameContainingIgnoreCase(String name);

    /**
     * Find communities administered by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of communities administered by the specified user
     */
    List<Community> findByAdminsId(String userId);

    /**
     * Count the number of communities created between the specified start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of communities created in the specified date range
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}