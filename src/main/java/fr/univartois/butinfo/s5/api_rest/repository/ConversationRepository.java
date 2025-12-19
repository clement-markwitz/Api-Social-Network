package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Conversation entities in MongoDB.
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    /**
     * Find conversations by member ID.
     *
     * @param memberId the ID of the member
     * @return list of conversations involving the specified member
     */
    List<Conversation> findByMembers_Id(String memberId);
}