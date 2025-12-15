package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    /**
     * L'ID de la conversation à laquelle ce message appartient.
     * C'est une "Référence Manuelle" à Conversation.id
     */
    @Indexed // Index pour récupérer les messages d'une conversation
    private Conversation conversation;

    /**
     * L'ID de l'utilisateur qui a envoyé ce message.
     * C'est votre "fromId".
     */
    @Indexed // Utile pour indexer les messages d'un expéditeur
    private User sender;

    private String text;

    /**
     * Liste d'URLs vers des pièces jointes (images, fichiers).
     */
    private List<String> attachments;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Liste des accusés de lecture.
     */
    @Indexed // Pour rechercher rapidement qui a lu le message
    private List<ReadReceipt> readBy;
}
