package fr.univartois.butinfo.s5.api_rest.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Token entities.
 */
public interface TokenRepository extends MongoRepository<Token, String> {
    /**
     * Find all valid tokens for a given user ID.
     * @param userId
     * @return
     */
    List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(String userId);

    /**
     * Find a token by its token string.
     * @param tokenValue
     * @return
     */
    Optional<Token> findByTokenValue(String tokenValue);
}