package fr.univartois.butinfo.s5.api_rest.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity representing a JWT token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens")
public class Token {

    @Id
    private String id; // Changé en 'private'

    @Indexed(unique = true)
    private String token;

    @Builder.Default
    private String tokenType = "BEARER";

    private boolean revoked;

    private boolean expired;

    @Indexed
    private String userId;
}