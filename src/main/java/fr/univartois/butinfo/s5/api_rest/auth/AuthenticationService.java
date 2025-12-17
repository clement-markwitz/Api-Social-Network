package fr.univartois.butinfo.s5.api_rest.auth;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserLoginDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import fr.univartois.butinfo.s5.api_rest.model.Interests;
import fr.univartois.butinfo.s5.api_rest.model.Preferences;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Service for handling authentication-related operations.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserMapper userMapper, TokenRepository tokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user.
     * @param request
     * @return
     */
    public User register(UserCreateDto request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username déjà utilisé");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.password()));

        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        if(user.getProfile().getLanguages() == null) user.getProfile().setLanguages(new ArrayList<>());
        user.setPrefs(new Preferences(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        user.setInterests(new Interests(new ArrayList<>(), new ArrayList<>()));

        return userRepository.save(user);
    }

    /**
     * Authenticate a user and return a JWT token.
     * @param request
     * @return
     */
    public User authenticate(UserLoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        revokeAllUserTokens(user);

        var jwtToken = jwtService.generateToken(user);

        saveUserToken(user, jwtToken);

        return user;
    }

    /**
     * Save a user token.
     * @param user
     * @param jwtToken
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .userId(user.getId())
                .token(jwtToken)
                .tokenType("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revoke all user tokens.
     * @param user
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllByUserIdAndExpiredFalseAndRevokedFalse(user.getId());
        if (validUserTokens.isEmpty()) return;

        tokenRepository.deleteAll(validUserTokens);
    }
}