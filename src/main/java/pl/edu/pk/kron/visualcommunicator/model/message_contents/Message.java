package pl.edu.pk.kron.visualcommunicator.model.message_contents;

import java.util.List;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID authorUserId;
    private final String content;
    private final UUID conversationId;

    public Message(UUID id, UUID authorUserId, String content, UUID conversationId) {
        this.id = id;
        this.authorUserId = authorUserId;
        this.content = content;
        this.conversationId = conversationId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthorUserId() {
        return authorUserId;
    }

    public String getContent() {
        return content;
    }

    public UUID getConversationId() {
        return conversationId;
    }
}
