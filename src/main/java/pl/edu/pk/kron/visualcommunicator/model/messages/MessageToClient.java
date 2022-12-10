package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.util.UUID;

public class MessageToClient {
    private final MessageType type;
    private UUID id;

    public MessageToClient(MessageType type) {
        this.type = type;
    }

    public MessageToClient withId(UUID id) {
        this.id = id;
        return this;
    }

    public MessageType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }
}
