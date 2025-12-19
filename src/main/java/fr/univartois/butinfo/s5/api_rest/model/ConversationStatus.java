package fr.univartois.butinfo.s5.api_rest.model;

public enum ConversationStatus {
    /**
     * Conversion 1 to 1 active.
     */
    ACTIVE,

    /**
     * A user has a pending invite to join the group.
     */
    PENDING_INVITE,

    /**
     * Conversation has been archived by all members.
     */
    ARCHIVED
}