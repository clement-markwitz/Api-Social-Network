package fr.univartois.butinfo.s5.api_rest.model;

public enum ConversationStatus {
    /**
     * Conversation 1-to-1 ou de groupe active.
     */
    ACTIVE,

    /**
     * Un utilisateur a été invité mais n'a pas encore rejoint (pour les groupes).
     */
    PENDING_INVITE,

    /**
     * Un utilisateur a quitté le groupe.
     */
    ARCHIVED
}