package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    /**
     * Trouve une relation de suivi spécifique.
     * @param followerId L'ID de l'utilisateur qui suit.
     * @param followingId L'ID de l'utilisateur qui est suivi.
     * @return Le Follow si trouvé.
     */
    Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);

    /**
     * Récupère toutes les relations de suivi où cet utilisateur est le suiveur.
     * (Liste des personnes que l'utilisateur suit - "Following").
     * @param followerId L'ID de l'utilisateur qui suit.
     * @return Liste des Follows.
     */
    List<Follow> findAllByFollowerId(String followerId);

    /**
     * Récupère toutes les relations de suivi où cet utilisateur est le suivi.
     * (Liste des personnes qui suivent l'utilisateur - "Followers").
     * @param followingId L'ID de l'utilisateur qui est suivi.
     * @return Liste des Follows.
     */
    List<Follow> findAllByFollowingId(String followingId);

    /**
     * Supprime une relation de suivi spécifique.
     * @param followerId L'ID de l'utilisateur qui suit.
     * @param followingId L'ID de l'utilisateur qui est suivi.
     * @return Le nombre d'enregistrements supprimés.
     */
    long deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}