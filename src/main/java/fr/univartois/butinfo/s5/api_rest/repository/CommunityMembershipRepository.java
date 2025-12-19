package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.CommunityMembership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMembershipRepository extends MongoRepository<CommunityMembership, String> {

    /**
     * Check if a user is a member of a specific community.
     * @param userId
     * @param communityId
     * @return true if the user is a member, false otherwise
     */
    boolean existsByUserIdAndCommunityId(String userId, String communityId);

    /**
     * Find the membership of a user in a specific community.
     * @param userId
     * @param communityId
     * @return
     */
    void deleteByUserIdAndCommunityId(String userId, String communityId);

    /**
     * Find all memberships for a specific community.
     * @param communityId
     * @return list of community memberships
     */
    List<CommunityMembership> findAllByCommunityId(String communityId);
}