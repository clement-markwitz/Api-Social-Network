package fr.univartois.butinfo.s5.api_rest.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(String userId);

    Optional<Token> findByToken(String token);
}