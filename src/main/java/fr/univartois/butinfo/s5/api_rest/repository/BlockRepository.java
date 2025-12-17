package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Block;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends MongoRepository<Block, String> {

    /**
     * Récupère la liste des blocages effectués par un utilisateur.
     * C'est la méthode que tu as demandée pour "connaitre les utilisateurs qu'il a bloqué".
     *
     * @param blockerId L'ID de l'utilisateur qui a bloqué.
     * @return Une liste d'objets Block.
     */
    List<Block> findAllByBlockerId(String blockerId);

    /**
     * Vérifie si un blocage existe déjà entre deux personnes.
     * Utile pour empêcher les doublons avant de créer un blocage.
     */
    boolean existsByBlockerIdAndBlockedId(String blockerId, String blockedId);

    /**
     * Trouve un blocage spécifique.
     * Utile si tu veux récupérer les détails d'un blocage (raison, date) via les IDs des users.
     */
    Optional<Block> findByBlockerIdAndBlockedId(String blockerId, String blockedId);

    /**
     * Supprime un blocage spécifique (Débloquer).
     * Permet de débloquer quelqu'un juste avec son ID, sans avoir besoin de l'ID de l'objet Block.
     */
    void deleteByBlockerIdAndBlockedId(String blockerId, String blockedId);

    /**
     * Récupère un blocage par l'id du blocker et l'id du blocked.
     */
    Optional<Block> findByIdAndBlockerIdAndBlockedId(String id, String blockerId, String blockedId);
}