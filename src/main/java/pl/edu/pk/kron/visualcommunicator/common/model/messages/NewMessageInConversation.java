package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Message;

import java.util.UUID;

public class NewMessageInConversation extends MessageToWebsocket {
    private final Message message;

    public NewMessageInConversation(UUID id, Message message) {
        super(MessageType.SERVER_NEW_MESSAGE, id);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
