package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Ban;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Ban entities in MongoDB.
 */
@Repository
public interface BanRepository extends MongoRepository<Ban, String> {
    // Find an active ban for a user
    /**
     * Finds an active ban for the specified user ID.
     *
     * @param userId the ID of the user
     * @return an Optional containing the active Ban if found, or empty if not found
     */
    Optional<Ban> findByUserIdAndActiveTrue(String userId);
}
