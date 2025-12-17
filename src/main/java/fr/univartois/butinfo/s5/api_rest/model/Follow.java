package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represent a "follow" relationship between two users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follows")
// Index unique pour empêcher un utilisateur de suivre 2x la même personne
@CompoundIndex(name = "follower_following_unique_idx", def = "{'followerId': 1, 'followingId': 1}", unique = true)
public class Follow {

    @Id
    private String id;

    /**
     * ID of the user who FOLLOWS.
     * Manual reference to the 'users' collection.
     */
    @Indexed // Indexe pour la requête : "Qui est-ce que je suis ?"
    @DBRef
    private User follower;

    /**
     * ID of the user being FOLLOWED.
     * Manual reference to the 'users' collection.
     */
    @Indexed // Indexe pour la requête : "Qui me suit ?"
    @DBRef
    private User following;

    @CreatedDate
    private LocalDateTime createdAt;

}
