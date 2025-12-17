package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.ConversationMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.ConversationRepository;
import fr.univartois.butinfo.s5.api_rest.repository.MessageRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;



    public Conversation createConversation(Conversation conversation,List<User> members) {
        conversation.setMembers(members);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    public List<ConversationSummaryDto> getMyConversations(String currentUserId) {
        List<Conversation> conversations = conversationRepository.findByMembers_Id(currentUserId);

        return conversations.stream().map(conv -> {
            Message lastMsg = messageRepository.findFirstByConversation_IdOrderByCreatedAtDesc(conv.getId())
                    .orElse(null);
            return conversationMapper.toSummaryDto(conv, lastMsg, currentUserId);
        }).toList();
    }

    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable"));
    }
    public void joinConversation(String conversationId, String currentUserId) {
        Conversation conversation = getConversationById(conversationId);

        // Vérifier si déjà membre
        boolean alreadyMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));

        if (alreadyMember) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous êtes déjà membre de cette conversation");
        }

        // Récupérer l'user et l'ajouter
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        conversation.getMembers().add(user);
        conversationRepository.save(conversation);
    }
    public void leaveConversation(String conversationId, String currentUserId) {
        Conversation conversation = getConversationById(conversationId);

        // Vérifier si membre
        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));

        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne faites pas partie de cette conversation");
        }

        // Retirer l'utilisateur de la liste
        conversation.getMembers().removeIf(u -> u.getId().equals(currentUserId));

        if (conversation.getMembers().isEmpty()) {
            // S'il n'y a plus personne, on supprime tout
            // 1. Supprimer les messages associés
            List<Message> messages = messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId);
            messageRepository.deleteAll(messages);

            // 2. Supprimer la conversation
            conversationRepository.delete(conversation);
        } else {
            // Sinon, on sauvegarde juste le départ
            conversationRepository.save(conversation);
        }
    }

    /**
     * Ajoute une liste d'utilisateurs à une conversation existante.
     * Réservé à l'initiateur.
     */
    public void addMembersToConversation(String conversationId, List<String> userIdsToAdd, String currentUserId) {
        Conversation conversation = getConversationById(conversationId);

        if (!conversation.getInitiator().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul l'initiateur peut ajouter des membres");
        }

        List<User> newUsers = userRepository.findAllById(userIdsToAdd);

        boolean updated = false;
        for (User userToAdd : newUsers) {
            boolean alreadyIn = conversation.getMembers().stream()
                    .anyMatch(member -> member.getId().equals(userToAdd.getId()));

            if (!alreadyIn) {
                conversation.getMembers().add(userToAdd);
                updated = true;
            }
        }

        if (updated) {
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }

    /**
     * Retire un utilisateur spécifique de la conversation.
     * Réservé à l'initiateur.
     */
    public void kickMemberFromConversation(String conversationId, String memberIdToRemove, String currentUserId) {
        Conversation conversation = getConversationById(conversationId);

        if (!conversation.getInitiator().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul l'initiateur peut exclure des membres");
        }

        if (memberIdToRemove.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisez 'leave' pour quitter la conversation");
        }

        boolean removed = conversation.getMembers().removeIf(u -> u.getId().equals(memberIdToRemove));

        if (removed) {
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cet utilisateur n'est pas dans la conversation");
        }
    }
}