package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represent a ban imposed on a user by a moderator.
 * The ban can be temporary or permanent.
 * A scheduled job will periodically check for expired bans and deactivate them.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bans")
@CompoundIndex(name = "user_active_idx", def = "{'userId': 1, 'active': 1}") // Index composé pour les requêtes courantes
public class Ban {

    @Id
    private String id;

    /**
     * ID from the banned user.
     * Ref to the 'users' collection.
     */
    @Indexed // Important d'indexer ce champ pour les recherches !
    @DBRef
    private User user;

    /**
     * ID from the moderator who issued the ban.
     * Ref to the 'users' collection.
     */
    @Indexed // Utile pour rechercher tous les bans d'un modérateur
    @DBRef
    private User moderator;

    private String reason;

    /**
     * Duration of the ban in days.
     * A value of 0 indicates a permanent ban.
     */
    private int durationDays;

    /**
     * Date and time when the ban starts.
     */
    private LocalDateTime startAt;

    /**
     * Date and time when the ban ends.
     * Null if the ban is permanent.
     */
    private LocalDateTime endAt;

    /**
     * Indicates whether the ban is currently active.
     */
    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}