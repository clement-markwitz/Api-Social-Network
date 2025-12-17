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

/**
 * Represent a blocking relationship between two users.
 * When user A blocks user B, user A (blocker) will not see any content from user B (blocked).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "block")
// Index composé pour la requête la plus fréquente :
// "Est-ce que l'utilisateur A (blockerId) a bloqué l'utilisateur B (blockedId) ?"
// L'unicité (unique = true) empêche de créer plusieurs fois le même blocage.
@CompoundIndex(name = "blocker_blocked_unique_idx", def = "{'blocker': 1, 'blocked': 1}", unique = true)
public class Block {

    @Id
    private String id;

    /**
     * ID of the user who is blocking.
     * Reference to the 'users' collection.
     */
    @Indexed // Index simple pour "Qui ai-je bloqué ?"
    @DBRef
    private User blocker;

    /**
     * ID of the user who is being blocked.
     * Reference to the 'users' collection.
     */
    @Indexed // Index simple pour "Qui m'a bloqué ?"
    @DBRef
    private User blocked;

    private String reason; // Optionnel

    @CreatedDate
    private LocalDateTime createdAt; // Date de création du blocage
}