package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Message;

public class NewMessageInConversation extends MessageToWebsocket {
    private final Message message;

    public NewMessageInConversation(Message message) {
        super(MessageType.SERVER_NEW_MESSAGE);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
