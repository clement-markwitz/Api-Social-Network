package fr.univartois.butinfo.s5.api_rest.config;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserCreateDto;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import fr.univartois.butinfo.s5.api_rest.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for initializing data.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    /**
     * Initialize the admin user if not present.
     *
     * @return a CommandLineRunner that initializes the admin user
     */
    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            String adminUsername = "robert duchmol";

            if (userRepository.findByUsername(adminUsername).isPresent()) {
                return;
            }

            UserCreateDto adminDto = new UserCreateDto(
                    adminUsername,
                    "robert_duchmol@gmail.com",
                    "GrosSecret1234!",
                    "RobertD"
            );

            User admin = authenticationService.register(adminDto);

            admin.setRole("ADMIN");

            userRepository.save(admin);
        };
    }
}