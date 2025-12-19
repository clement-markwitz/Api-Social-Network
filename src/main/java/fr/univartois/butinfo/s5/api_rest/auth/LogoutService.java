package fr.univartois.butinfo.s5.api_rest.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling user logout.
 */
@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Constructor for LogoutService.
     * @param tokenRepository the token repository
     */
    public LogoutService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Handle user logout by invalidating the JWT token.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param authentication the authentication object
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);
        Optional<Token> token = tokenRepository.findByTokenValue(jwt);

        token.ifPresent(value -> tokenRepository.deleteById(value.getId()));
    }
}