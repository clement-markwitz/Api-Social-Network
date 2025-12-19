package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Find a conversation that contains all specified member IDs.
     * @param membersIds
     * @return
     */
    @Query("{ 'members.$id': { $all: ?0 } }")
    Optional<Conversation> findByMembersIdsContainsAll(List<String> membersIds);
}