package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.util.UUID;

public class GetMessages extends MessageFromClient {
    private final UUID conversationId;

    public GetMessages(UUID conversationId) {
        super(MessageType.CLIENT_GET_MESSAGES);
        this.conversationId = conversationId;
    }

    public UUID getConversationId() {
        return conversationId;
    }
}
