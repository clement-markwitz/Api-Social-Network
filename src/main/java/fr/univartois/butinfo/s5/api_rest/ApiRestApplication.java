package fr.univartois.butinfo.s5.api_rest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class ApiRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRestApplication.class, args);
    }

    // --- C'est ici que se fait le nettoyage ---
    @Bean
    public CommandLineRunner cleaner(MongoTemplate mongoTemplate) {
        return args -> {
            System.out.println("---------- ⚠️ NETTOYAGE EN COURS ⚠️ ----------");

            // Cette ligne supprime la collection corrompue
            mongoTemplate.dropCollection("reactions");

            System.out.println("---------- ✅ COLLECTION REACTIONS SUPPRIMÉE ----------");
        };
    }
}