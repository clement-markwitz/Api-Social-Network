package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Message entities in MongoDB.
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    /**
     * Finds all messages associated with a specific conversation ID, ordered by creation date in ascending order.
     *
     * @param conversationId the ID of the conversation
     * @return a list of messages belonging to the specified conversation
     */
    List<Message> findByConversation_IdOrderByCreatedAtAsc(String conversationId);

    /**
     * Finds the most recent message associated with a specific conversation ID.
     *
     * @param conversationId the ID of the conversation
     * @return an Optional containing the most recent message if found, otherwise empty
     */
    Optional<Message> findFirstByConversation_IdOrderByCreatedAtDesc(String conversationId);

    /**
     * Counts the number of messages created between the specified start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of messages created in the specified date range
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}