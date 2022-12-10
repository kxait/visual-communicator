package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Message;

public class NewMessageInConversation extends MessageToClient {
    private final Message message;

    public NewMessageInConversation(Message message) {
        super(MessageType.SERVER_NEW_MESSAGE);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
