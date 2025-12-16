package fr.univartois.butinfo.s5.api_rest.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens")
public class Token {

    @Id
    private String id; // Changé en 'private'

    @Indexed(unique = true)
    private String tokenValue; // Renommé (token -> tokenValue) et mis en 'private'

    @Builder.Default // Ajouté pour garder la valeur "BEARER" par défaut avec le Builder
    private String tokenType = "BEARER"; // Mis en 'private'

    private boolean revoked; // Mis en 'private'

    private boolean expired; // Mis en 'private'

    @Indexed
    private String userId; // Mis en 'private'
}