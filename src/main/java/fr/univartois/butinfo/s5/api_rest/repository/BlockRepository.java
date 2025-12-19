package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Block;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Block entities in MongoDB.
 */
@Repository
public interface BlockRepository extends MongoRepository<Block, String> {

    /**
     * Get all blocks initiated by a specific user.
     * @param blockerId
     * @return
     */
    List<Block> findAllByBlockerId(String blockerId);

    /**
     * Get all blocks received by a specific user.
     * @param blockedId
     * @return
     */
    List<Block> findAllByBlockedId(String blockedId);

    /**
     * Test if a block exists between two users.
     * @param blockerId
     * @param blockedId
     * @return
     */
    boolean existsByBlockerIdAndBlockedId(String blockerId, String blockedId);

    /**
     * Get a block between two users.
     * @param blockerId
     * @param blockedId
     * @return
     */
    Optional<Block> findByBlockerIdAndBlockedId(String blockerId, String blockedId);

    /**
     * Delete a block between two users.
     * @param blockerId
     * @param blockedId
     */
    void deleteByBlockerIdAndBlockedId(String blockerId, String blockedId);

    /**
     * Find a block by its id, blocker id, and blocked id.
     * @param id
     * @param blockerId
     * @param blockedId
     * @return
     */
    Optional<Block> findByIdAndBlockerIdAndBlockedId(String id, String blockerId, String blockedId);
}