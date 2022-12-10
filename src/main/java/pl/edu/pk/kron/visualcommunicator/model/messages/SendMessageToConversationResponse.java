package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Message;

public class SendMessageToConversationResponse extends MessageToClient {
    private final Message message;

    public SendMessageToConversationResponse(Message message) {
        super(MessageType.CLIENT_SEND_MESSAGE);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
