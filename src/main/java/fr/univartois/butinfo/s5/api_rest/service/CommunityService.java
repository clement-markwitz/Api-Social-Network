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
     * @param community
     * @param user
     */
    public boolean isCommunityAdmin(Community community, User user) {
        return community.getAdmins() != null && community.getAdmins().stream()
                .anyMatch(admin -> admin.getId().equals(user.getId()));
    }

    /**
     * Get all communities.
     * @return
     */
    public List<Community> getAll() {
        return communityRepository.findAll();
    }

    /**
     * Get a community by its ID.
     * @param id
     * @return
     */
    public Community getById(String id) {
        return communityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
    }

    /**
     * Create a new community.
     * @param community
     * @return
     */
    public Community createCommunity(Community community) {
        return communityRepository.save(community);
    }

    /**
     * Update an existing community.
     * @param community
     * @return
     */
    public Community updateCommunity(Community community) {
        return communityRepository.save(community);
    }

    /**
     * Delete a community by its ID.
     * @param id
     * @return
     */
    public void deleteCommunity(String id) {
        if (!communityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found");
        }
        communityRepository.deleteById(id);
    }

    /**
     * 2. Implémentation de l'abonnement (Follow)
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

    public void removeMemberFromCommunity(String communityId, String userid) {
        communityMembershipRepository.deleteByUserIdAndCommunityId(userid, communityId);
    }
}
