package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends MongoRepository<Community, String> {

    Optional<Community> findByName(String name);

    boolean existsByName(String name);

    List<Community> findByNameContainingIgnoreCase(String name);

    List<Community> findByAdminsId(String userId);
}