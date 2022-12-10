package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

public class MessageToWebsocket {
    private final MessageType type;

    public MessageToWebsocket(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
