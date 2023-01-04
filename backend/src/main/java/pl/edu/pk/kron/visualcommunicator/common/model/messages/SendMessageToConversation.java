package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class SendMessageToConversation extends MessageFromWebsocket {
    private final UUID conversationId;
    private final String content;


    public SendMessageToConversation(UUID id, UUID conversationId, String content) {
        super(id, MessageType.CLIENT_SEND_MESSAGE);

        this.conversationId = conversationId;
        this.content = content;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public String getContent() {
        return content;
    }
}
