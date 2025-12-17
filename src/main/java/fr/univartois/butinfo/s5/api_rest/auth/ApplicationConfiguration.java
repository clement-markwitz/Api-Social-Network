package fr.univartois.butinfo.s5.api_rest.auth;

import fr.univartois.butinfo.s5.api_rest.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for application security.
 */
@Configuration
public class ApplicationConfiguration {

    private final UserService utilisateurService;

    public ApplicationConfiguration(UserService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Get the authentication manager bean.
     * @param config
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Get the authentication provider bean.
     * @return
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(utilisateurService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Get the user details service bean.
     * @return
     */
    @Bean
    UserDetailsService userDetailsService() {
        return utilisateurService;
    }

    /**
     * Get the password encoder bean.
     * @return
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
