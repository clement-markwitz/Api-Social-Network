package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.*;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for user-related endpoints.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service
     * @param userMapper the user mapper
     */
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Get the authenticated user's private profile.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with UserPrivateProfileDto
     */
    @GetMapping("/me")
    @Operation(summary = "Récupérer le profil privé du user connecté", description = "Permet de récupérer les informations privées du profil de l'utilisateur actuellement authentifié.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil privé récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié")
    })
    public ResponseEntity<UserPrivateProfileDto> authenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
        }

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userMapper.toPrivateProfileDto(user));
    }

    /**
     * Get a user's public profile by ID.
     *
     * @param id the user's ID
     * @return UserPublicProfileDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer le profil public d'un utilisateur par ID", description = "Permet de récupérer les informations publiques du profil d'un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil public récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public UserPublicProfileDto getUser(@PathVariable String id) {
        User user = userService.getById(id);
        return userMapper.toPublicProfileDto(user);
    }

    /**
     * Get all users as summaries.
     *
     * @return List of UserSummaryDto
     */
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère une liste de tous les utilisateurs au format résumé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès")
    })
    public List<UserSummaryDto> getAllUsers() {
        List<User> users =  userService.getAll();
        return users.stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user's ID
     * @return Boolean indicating success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur par ID", description = "Permet de supprimer un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "403", description = "Droits insuffisants pour supprimer cet utilisateur"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public Boolean deleteUser(@PathVariable String id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        userService.checkUserRights(id, currentUser);

        return userService.delete(id);
    }

    /**
     * Update a user's profile.
     *
     * @param id the user's ID
     * @param updateDto the profile update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated UserPublicProfileDto
     */
    @PutMapping("/{id}/profile")
    @Operation(summary = "Mettre à jour le profil d'un utilisateur", description = "Permet de mettre à jour les informations du profil d'un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Droits insuffisants pour mettre à jour ce profil"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserPublicProfileDto> updateProfile(@PathVariable String id, @Valid @RequestBody ProfileUpdateDto updateDto, Authentication authentication) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = (User) authentication.getPrincipal();
        userService.checkUserRights(existingUser.getId(), currentUser);

        userMapper.updateProfileFromDto(updateDto, existingUser.getProfile());

        User updatedUser = userService.updateUser(existingUser);

        return ResponseEntity.ok(userMapper.toPublicProfileDto(updatedUser));
    }

    /**
     * Update a user's preferences.
     *
     * @param id the user's ID
     * @param updateDto the preferences update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated PreferencesDto
     */
    @PutMapping("/{id}/preferences")
    @Operation(summary = "Mettre à jour les préférences d'un utilisateur", description = "Permet de mettre à jour les préférences d'un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préférences mises à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Droits insuffisants pour mettre à jour ces préférences"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<PreferencesDto> updatePreferences(@PathVariable String id, @RequestBody PreferencesUpdateDto updateDto, Authentication authentication) {
        User existingUser = userService.getById(id);
        if (existingUser == null) return ResponseEntity.notFound().build();

        User currentUser = (User) authentication.getPrincipal();
        userService.checkUserRights(existingUser.getId(), currentUser);

        userMapper.updatePreferencesFromDto(updateDto, existingUser.getPrefs());
        User updatedUser = userService.updateUser(existingUser);

        return ResponseEntity.ok(userMapper.toPreferencesDto(updatedUser.getPrefs()));
    }

    /**
     * Update a user's interests.
     *
     * @param id the user's ID
     * @param updateDto the interests update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated InterestsDto
     */
    @PutMapping("/{id}/interests")
    @Operation(summary = "Mettre à jour les intérêts d'un utilisateur", description = "Permet de mettre à jour les intérêts d'un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intérêts mis à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Droits insuffisants pour mettre à jour ces intérêts"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<InterestsDto> updateInterests(@PathVariable String id, @RequestBody InterestsUpdateDto updateDto, Authentication authentication) {
        User existingUser = userService.getById(id);
        if (existingUser == null) return ResponseEntity.notFound().build();

        User currentUser = (User) authentication.getPrincipal();
        userService.checkUserRights(existingUser.getId(), currentUser);

        userMapper.updateInterestsFromDto(updateDto, existingUser.getInterests());
        User updatedUser = userService.updateUser(existingUser);

        return ResponseEntity.ok(userMapper.toInterestsDto(updatedUser.getInterests()));
    }


    /**
     * Search for user profiles by query.
     *
     * @param query the search query
     * @return List of UserSummaryDto matching the query
     */
    @GetMapping("/search")
    @Operation(summary = "Rechercher des profils d'utilisateurs", description = "Permet de rechercher des profils d'utilisateurs en fonction d'une requête de recherche.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profils d'utilisateurs récupérés avec succès")
    })
    public List<UserSummaryDto> searchProfiles(@RequestParam("q") String query) {
        List<User> users = userService.searchUsers(query);

        return users.stream().map(userMapper::toSummaryDto).toList();
    }
}