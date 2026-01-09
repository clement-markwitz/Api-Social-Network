package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Page;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Page entities in MongoDB.
 */
@Repository
public interface PageRepository extends MongoRepository<Page, String> {
    /**
     * Check if a page with the given name exists.
     *
     * @param name the name of the page
     * @return true if a page with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Retrieve a list of random pages.
     *
     * @return a list of random Page entities
     */
    @Aggregation(pipeline = {
            "{ '$sample': { 'size': ?0 } }"
    })
    List<Page> findRandomPages(int count);
}