package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.List;
import java.util.UUID;

public class CreateConversation extends MessageFromWebsocket {
    private final List<UUID> recipients;
    private final String name;
    public CreateConversation(List<UUID> recipients, String name) {
        super(MessageType.CLIENT_CREATE_NEW_CONVERSATION);
        this.recipients = recipients;
        this.name = name;
    }

    public List<UUID> getRecipients() {
        return recipients;
    }

    public String getName() {
        return name;
    }
}
