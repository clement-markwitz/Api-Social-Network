package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blocked")
// Index composé pour la requête la plus fréquente :
// "Est-ce que l'utilisateur A (blockerId) a bloqué l'utilisateur B (blockedId) ?"
// L'unicité (unique = true) empêche de créer plusieurs fois le même blocage.
@CompoundIndex(name = "blocker_blocked_unique_idx", def = "{'blockerId': 1, 'blockedId': 1}", unique = true)
public class Blocked {

    @Id
    private String id;

    /**
     * L'ID de l'utilisateur qui effectue le blocage.
     * Référence manuelle à la collection 'users'.
     */
    @Indexed // Index simple pour "Qui ai-je bloqué ?"
    private User blocker;

    /**
     * L'ID de l'utilisateur qui est bloqué.
     * Référence manuelle à la collection 'users'.
     */
    @Indexed // Index simple pour "Qui m'a bloqué ?"
    private User blocked;

    private String reason; // Optionnel

    @CreatedDate
    private LocalDateTime createdAt; // Date de création du blocage
}