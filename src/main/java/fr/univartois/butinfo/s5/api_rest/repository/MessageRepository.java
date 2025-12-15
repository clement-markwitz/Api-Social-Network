package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    // Trouve les messages via l'ID de la conversation imbriquée
    List<Message> findByConversation_IdOrderByCreatedAtAsc(String conversationId);

    // Récupère le dernier message (pour l'aperçu)
    Optional<Message> findFirstByConversation_IdOrderByCreatedAtDesc(String conversationId);
}