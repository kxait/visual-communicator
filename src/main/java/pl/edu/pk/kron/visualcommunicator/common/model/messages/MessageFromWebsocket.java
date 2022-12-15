package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class MessageFromWebsocket {
    private final UUID id;
    private final MessageType type;
    public MessageFromWebsocket(UUID id, MessageType type) {
        this.id = id;
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }
}
