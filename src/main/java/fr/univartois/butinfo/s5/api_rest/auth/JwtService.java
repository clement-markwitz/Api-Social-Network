package fr.univartois.butinfo.s5.api_rest.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT operations.
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private TokenRepository tokenRepository;

    /**
     * Constructor for JwtService.
     *
     * @param tokenRepository the token repository
     */
    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Generate a JWT token for the given user details.
     *
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * Generate a JWT token with extra claims for the given user details.
     *
     * @param extraClaims the extra claims to include in the token
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Validate the given JWT token against the user details.
     *
     * @param token the JWT token
     * @param userDetails the user details
     * @return the validity of the token
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        boolean isTokenPresentInDb = tokenRepository.findByTokenValue(token).isPresent();

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && isTokenPresentInDb;
    }

    /**
     * Check if the given JWT token is expired.
     *
     * @param token the JWT token
     * @return the expiration status of the token
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract the expiration date from the given JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract the username from the given JWT token.
     *
     * @param token the token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a specific claim from the given JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver function to resolve the claim
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from the given JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get the signing key for JWT.
     *
     * @return the secret key
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}