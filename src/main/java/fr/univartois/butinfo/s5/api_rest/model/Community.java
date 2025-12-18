package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "groups")
public class Community {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String description;
    private String avatarUrl;

    /**
     * Liste des ID des utilisateurs qui gèrent la communauté.
     * (Standardisé à partir de votre champ 'admin' du JSON)
     */
    @DBRef
    private List<User> admins;

    /**
     * Nombre total de membres (dénormalisé).
     * (Votre champ 'membersCount' du JSON)
     */
    private int memberCount;

    private List<String> topics;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Ajoute un administrateur à la communauté de manière sécurisée.
     */
    public void addAdmin(User user) {
        if (this.admins == null) {
            this.admins = new ArrayList<>();
        }
        this.admins.add(user);
    }

    /**
     * Supprime un administrateur en toute sécurité.
     */
    public void removeAdmin(User user) {
        if (this.admins != null) {
            this.admins.remove(user);
        }
    }

    /**
     * Ajoute un topic en initialisant la liste si nécessaire.
     */
    public void addTopic(String topic) {
        if (this.topics == null) {
            this.topics = new ArrayList<>();
        }
        this.topics.add(topic);
    }

    /**
     * Supprime un topic en toute sécurité.
     */
    public void removeTopic(String topic) {
        if (this.topics != null) {
            this.topics.remove(topic);
        }
    }
}