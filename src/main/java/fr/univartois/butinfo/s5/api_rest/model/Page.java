package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pages")
public class Page {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String description;
    private String avatarUrl;

    /**
     * Liste des ID des utilisateurs qui gèrent la page (poster, modifier, etc.).
     * (Votre champ 'admins' du JSON)
     */
    private List<User> admins;

    /**
     * Nombre total d'abonnés. C'est de la "dénormalisation".
     * Doit être mis à jour manuellement (ex: +1 ou -1)
     * à chaque création/suppression de PageSubscription.
     */
    private int followerCount;

    private List<String> topics;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}