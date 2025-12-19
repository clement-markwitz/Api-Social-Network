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

/**
 * Represent a community within the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "communities")
public class Community {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String description;
    private String avatarUrl;

    /**
     * List of administrators of the community.
     */
    @DBRef
    private List<User> admins;

    /**
     * Number of members in the community.
     */
    private int memberCount;

    private List<String> topics;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Add an administrator by initializing the list if necessary.
     * @param user the user to add as admin
     */
    public void addAdmin(User user) {
        if (this.admins == null) {
            this.admins = new ArrayList<>();
        }
        this.admins.add(user);
    }

    /**
     * Remove an administrator safely.
     * @param user the user to remove from admins
     */
    public void removeAdmin(User user) {
        if (this.admins != null) {
            this.admins.remove(user);
        }
    }

    /**
     * Add a topic by initializing the list if necessary.
     * @param topic the topic to add
     */
    public void addTopic(String topic) {
        if (this.topics == null) {
            this.topics = new ArrayList<>();
        }
        this.topics.add(topic);
    }

    /**
     * Remove a topic safely.
     * @param topic the topic to remove
     */
    public void removeTopic(String topic) {
        if (this.topics != null) {
            this.topics.remove(topic);
        }
    }
}