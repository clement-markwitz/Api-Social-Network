package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserMapper userMapper;

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId();
        }
        throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié.");
    }

    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.followUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.unfollowUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing() {
        String followerId = getAuthenticatedUserId();
        List<User> users = followService.getFollowing(followerId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).collect(Collectors.toList()));
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers() {
        String followingId = getAuthenticatedUserId();
        List<User> users = followService.getFollowers(followingId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).collect(Collectors.toList()));
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserSummaryDto>> getFriends(@PathVariable String userId) {
        List<User> friends = followService.getFriends(userId);
        return ResponseEntity.ok(friends.stream().map(userMapper::toSummaryDto).collect(Collectors.toList()));
    }
}