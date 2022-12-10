package pl.edu.pk.kron.visualcommunicator.model.messages;

public class MessageToClientCombined {
    private final MessageToClient message;
    private final MessageToClientMetadata metadata;

    public MessageToClientCombined(MessageToClient message, MessageToClientMetadata metadata) {
        this.message = message;
        this.metadata = metadata;
    }

    public MessageToClient getMessage() {
        return message;
    }

    public MessageToClientMetadata getMetadata() {
        return metadata;
    }
}
