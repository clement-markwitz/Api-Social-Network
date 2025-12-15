package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
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
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    /**
     * Liste des ID de tous les utilisateurs membres de cette conversation.
     * C'est ce qui permet les discussions de groupe.
     */
    @Indexed // Index pour trouver les conversations d'un utilisateur
    private List<User> members;

    /**
     * L'ID de l'utilisateur qui a initié la conversation.
     */
    private User initiator;

    /**
     * Statut de la conversation (ex: ACTIVE, PENDING_INVITE, LEFT_GROUP).
     */
    private ConversationStatus status;

    /**
     * Nom de la conversation (utile pour les groupes).
     */
    private String name;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
