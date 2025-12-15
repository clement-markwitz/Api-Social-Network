package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPrivateProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPublicProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
     * Register a new user.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserPrivateProfileDto register(@Valid @RequestBody UserCreateDto userCreateDto) {
        User createdUser = userService.create(userCreateDto);
        return userMapper.toPrivateProfileDto(createdUser);
    }

    /**
     * Get a user's public profile by ID.
     */
    @GetMapping("/{id}")
    public UserPublicProfileDto getUser(@PathVariable String id) {
        User user = userService.getById(id);
        return userMapper.toPublicProfileDto(user);
    }

    /**
     * Get a list of all users (summarized).
     */
    @GetMapping
    public List<UserSummaryDto> getAllUsers() {
        List<User> users =  userService.getAll();
        return users.stream()
                .map(userMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete a user by ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }
}