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
     *
     * @param userId the user ID
     * @return a list of valid tokens
     */
    List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(String userId);

    /**
     * Find a token by its token string.
     *
     * @param tokenValue the token string
     * @return an Optional containing the Token if found, or empty if not found
     */
    Optional<Token> findByTokenValue(String tokenValue);
}