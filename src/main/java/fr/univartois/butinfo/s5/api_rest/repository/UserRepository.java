package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities in MongoDB.
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Find a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find users whose profile pseudo contains the given string, case insensitive.
     *
     * @param pseudo the pseudo substring to search for
     * @return a list of Users whose profile pseudo contains the given string
     */
    List<User> findByProfilePseudoContainingIgnoreCase(String pseudo);

    /**
     * Count the number of users created between the specified start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of users created in the specified date range
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Retrieve a list of random users.
     *
     * @return a list of random User entities
     */
    @Aggregation(pipeline = {
            "{ '$match': { '_id': { '$ne': ?1 } } }", // 1. On exclut l'utilisateur qui fait la demande
            "{ '$sample': { 'size': ?0 } }"           // 2. On prend 'count' documents au hasard
    })
    List<User> findRandomUsers(int count, String excludedUserId);
}