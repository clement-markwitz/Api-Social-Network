package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bans")
@CompoundIndex(name = "user_active_idx", def = "{'userId': 1, 'active': 1}") // Index composé pour les requêtes courantes
public class Ban {

    @Id
    private String id;

    /**
     * L'ID de l'utilisateur qui est banni.
     * Référence Manuelle à la collection 'users'.
     */
    @Indexed // TRÈS important d'indexer ce champ pour les recherches !
    private String userId;

    /**
     * L'ID de l'utilisateur (modérateur) qui a émis le ban.
     * Référence Manuelle à la collection 'users'.
     */
    @Indexed // Utile pour rechercher tous les bans d'un modérateur
    private String moderatorId;

    private String reason;

    /**
     * Durée du ban en jours.
     */
    private int durationDays;

    /**
     * Date et heure de début effectif du ban.
     */
    private LocalDateTime startAt;

    /**
     * Date et heure de fin calculée du ban.
     */
    private LocalDateTime endAt;

    /**
     * Indique si le ban est actuellement en vigueur.
     * Un "job" va passer ce champ à 'false' lorsque 'endAt' est dépassé.
     */
    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt; // Date de création de l'enregistrement du ban

    @LastModifiedDate
    private LocalDateTime updatedAt; // Date de dernière modification (ex: si le ban est révoqué)
}