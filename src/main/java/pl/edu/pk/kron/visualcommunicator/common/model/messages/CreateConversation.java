package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.List;
import java.util.UUID;

public class CreateConversation extends MessageFromWebsocket {
    private final List<UUID> recipients;
    // string name
    public CreateConversation(List<UUID> recipients) {
        super(MessageType.CLIENT_CREATE_NEW_CONVERSATION);
        this.recipients = recipients;
    }

    public List<UUID> getRecipients() {
        return recipients;
    }
}
