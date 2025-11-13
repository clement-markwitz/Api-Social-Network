package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Pour un pattern de construction plus fluide lors de tests avec AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id; // Correction : Doit être String pour l'ObjectId

    @Indexed(unique = true) // Important pour la performance et l'unicité
    private String username;

    @Indexed(unique = true) // Important pour la performance et l'unicité
    private String email;

    private String password;

    private String role;

    // Objets embarqués
    private Profile profile;
    private Preferences prefs;
    private Interests interests;

    private boolean banned;

    @CreatedDate // Géré automatiquement si l'audit est activé
    private LocalDateTime createdAt;

    @LastModifiedDate // Géré automatiquement si l'audit est activé
    private LocalDateTime updatedAt;
}
