package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.model.PageSubscription;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing PageSubscription entities in MongoDB.
 */
@Repository
public interface PageSubscriptionRepository extends MongoRepository<PageSubscription, String> {
    /**
     * Check if a subscription exists for a given user and page.
     *
     * @param user the user
     * @param page the page
     * @return the boolean indicating if the subscription exists
     */
    boolean existsByUserAndPage(User user, Page page);

    /**
     * Find a subscription by user and page.
     *
     * @param user the user
     * @param page the page
     * @return an Optional containing the PageSubscription if found, or empty if not found
     */
    Optional<PageSubscription> findByUserAndPage(User user, Page page);
}
