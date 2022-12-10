package pl.edu.pk.kron.visualcommunicator.model.messages;

public class MessageFromClientCombined {
    private final MessageFromClient message;
    private final MessageFromClientMetadata metadata;

    public MessageFromClientCombined(MessageFromClient message, MessageFromClientMetadata metadata) {
        this.message = message;
        this.metadata = metadata;
    }

    public MessageFromClient getMessage() {
        return message;
    }

    public MessageFromClientMetadata getMetadata() {
        return metadata;
    }
}
