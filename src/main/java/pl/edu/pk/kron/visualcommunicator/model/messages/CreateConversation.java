package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.util.List;
import java.util.UUID;

public class CreateConversation extends MessageFromClient {
    private final List<UUID> recipients;

    public CreateConversation(List<UUID> recipients) {
        super(MessageType.CLIENT_CREATE_NEW_CONVERSATION);
        this.recipients = recipients;
    }

    public List<UUID> getRecipients() {
        return recipients;
    }
}
