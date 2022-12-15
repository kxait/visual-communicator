package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetMessages extends MessageFromWebsocket {
    private final UUID conversationId;

    public GetMessages(UUID id, UUID conversationId) {
        super(id, MessageType.CLIENT_GET_MESSAGES);
        this.conversationId = conversationId;
    }

    public UUID getConversationId() {
        return conversationId;
    }
}
