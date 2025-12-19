package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.CommunityMembership;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.CommunityMembershipRepository;
import fr.univartois.butinfo.s5.api_rest.repository.CommunityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing communities.
 */
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository communityMembershipRepository;

    public CommunityService(CommunityRepository communityRepository, CommunityMembershipRepository communityMembershipRepository) {
        this.communityRepository = communityRepository;
        this.communityMembershipRepository = communityMembershipRepository;
    }

    /**
     * Verify if the user has admin rights in the community.
     * @param community the community
     * @param user the user
     */
    public boolean isCommunityAdmin(Community community, User user) {
        return community.getAdmins() != null && community.getAdmins().stream()
                .anyMatch(admin -> admin.getId().equals(user.getId()));
    }

    /**
     * Get all communities.
     *
     * @return list of all communities
     */
    public List<Community> getAll() {
        return communityRepository.findAll();
    }

    /**
     * Get a community by its ID.
     * @param id the community ID
     * @return the community
     */
    public Community getById(String id) {
        return communityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
    }

    /**
     * Create a new community.
     * @param community the community
     * @return the created community
     */
    public Community createCommunity(Community community) {
        return communityRepository.save(community);
    }

    /**
     * Update an existing community.
     * @param community the community
     * @return the updated community
     */
    public Community updateCommunity(Community community) {
        return communityRepository.save(community);
    }

    /**
     * Delete a community by its ID.
     * @param id the community ID
     */
    public void deleteCommunity(String id) {
        if (!communityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found");
        }
        communityRepository.deleteById(id);
    }

    /**
     * Add a member to a community.
     *
     * @param communityId the community ID
     * @param user the user to be added
     */
    public void addMemberToCommunity(String communityId, User user) {

        Community community = getById(communityId);
        if (communityMembershipRepository.existsByUserIdAndCommunityId(user.getId(), communityId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous suivez déjà cette communauté.");
        }

        CommunityMembership membership = new CommunityMembership();
        membership.setCommunity(community);
        membership.setUser(user);
        membership.setCreatedAt(LocalDateTime.now());

        communityMembershipRepository.save(membership);
    }

    /**
     * Remove a member from a community.
     *
     * @param communityId the community ID
     * @param userid the user ID to be removed
     */
    public void removeMemberFromCommunity(String communityId, String userid) {
        communityMembershipRepository.deleteByUserIdAndCommunityId(userid, communityId);
    }
}
