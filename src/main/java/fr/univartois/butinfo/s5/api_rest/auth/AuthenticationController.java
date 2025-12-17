package fr.univartois.butinfo.s5.api_rest.auth;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserLoginDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPrivateProfileDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserMapper userMapper) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un nouvel utilisateur", description = "Permet de créer un nouveau utilisateur avec les informations fournies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    public UserPrivateProfileDto register(@Valid @RequestBody UserCreateDto dto) {
        User registeredUser = authenticationService.register(dto);
        return userMapper.toPrivateProfileDto(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur", description = "Permet à un utilisateur de se connecter et de recevoir un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Échec de l'authentification")
    })
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody UserLoginDto dto) {
        User authenticatedUser = authenticationService.authenticate(dto);
        String token = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok(Map.of("token", token));
    }
}