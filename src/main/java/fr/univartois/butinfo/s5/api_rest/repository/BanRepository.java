package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Ban;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BanRepository extends MongoRepository<Ban, String> {
    // Find an active ban for a user
    Optional<Ban> findByUserAndActiveTrue(String userId);
}
