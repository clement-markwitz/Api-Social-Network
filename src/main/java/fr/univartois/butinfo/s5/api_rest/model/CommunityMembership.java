package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communityMemberships")
@CompoundIndex(name = "user_community_unique_idx", def = "{'userId': 1, 'communityId': 1}", unique = true)
public class CommunityMembership {

    @Id
    private String id;

    @Indexed
    private String userId; // L'utilisateur qui rejoint

    @Indexed
    private String communityId; // La communauté qu'il rejoint

    @CreatedDate
    private LocalDateTime createdAt;
}