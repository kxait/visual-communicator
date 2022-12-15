package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class MessageToWebsocket {
    private final MessageType type;
    private final UUID id;

    public MessageToWebsocket(MessageType type, UUID id) {
        this.type = type;
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }
}
