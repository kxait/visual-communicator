package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

public class MessageFromWebsocket {
    private final MessageType type;
    public MessageFromWebsocket(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
