package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserPrivateProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPublicProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
// J'ai supprimé l'import "java.util.stream.Collectors" qui ne sert plus à rien

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Récupérer les informations de l'utilisateur connecté (Route /me du cours).
     */
    @GetMapping("/me")
    public ResponseEntity<UserPrivateProfileDto> authenticatedUser(Authentication authentication) {
        // CORRECTION : Cast sécurisé (Pattern Matching) comme dans tes autres fichiers
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(userMapper.toPrivateProfileDto(user));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
    }

    @GetMapping("/{id}")
    public UserPublicProfileDto getUser(@PathVariable String id) {
        User user = userService.getById(id);
        return userMapper.toPublicProfileDto(user);
    }

    @GetMapping
    public List<UserSummaryDto> getAllUsers() {
        List<User> users =  userService.getAll();
        // CORRECTION SONAR : Remplacement de .collect(Collectors.toList()) par .toList()
        return users.stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }
}