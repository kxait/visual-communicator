package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.net.InetSocketAddress;
import java.util.UUID;

public class MessageFromClient {
    private final MessageType type;
    private UUID id;

    public MessageFromClient(MessageType type) {
        this.type = type;
    }

    public MessageFromClient withId(UUID id) {
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
