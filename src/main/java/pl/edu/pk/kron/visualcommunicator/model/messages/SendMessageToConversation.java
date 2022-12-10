package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.util.UUID;

public class SendMessageToConversation extends MessageFromClient {
    private final UUID conversationId;
    private final String content;


    public SendMessageToConversation(UUID conversationId, String content) {
        super(MessageType.CLIENT_SEND_MESSAGE);

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
