package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blocked")
// CORRECTION : J'ai mis à jour le nom du champ dans l'index aussi ('blocked' -> 'blockedUser')
// Note : Les champs s'appellent 'blocker' et 'blockedUser' dans ta classe, pas 'blockerId'.
@CompoundIndex(name = "blocker_blocked_unique_idx", def = "{'blocker': 1, 'blockedUser': 1}", unique = true)
public class Blocked {

    @Id
    private String id;

    /**
     * L'ID de l'utilisateur qui effectue le blocage.
     */
    @Indexed
    @DBRef
    private User blocker;

    /**
     * L'ID de l'utilisateur qui est bloqué.
     */
    @Indexed
    @DBRef
    // CORRECTION SONAR : Renommé de 'blocked' à 'blockedUser' pour éviter le conflit avec le nom de la classe
    private User blockedUser;

    private String reason;

    @CreatedDate
    private LocalDateTime createdAt;
}