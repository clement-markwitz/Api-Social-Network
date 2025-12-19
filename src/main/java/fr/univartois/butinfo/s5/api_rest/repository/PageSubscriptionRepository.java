package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.model.PageSubscription;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageSubscriptionRepository extends MongoRepository<PageSubscription, String> {
    boolean existsByUserAndPage(User user, Page page);
    Optional<PageSubscription> findByUserAndPage(User user, Page page);
}
