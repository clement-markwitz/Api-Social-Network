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
     * CORRECTION : On stocke les IDs des admins (String) et non les objets User.
     * Cela correspond à ce que ton Mapper essaie de faire.
     */
    private List<String> adminIds;

    private int followerCount;

    private List<String> topics;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}