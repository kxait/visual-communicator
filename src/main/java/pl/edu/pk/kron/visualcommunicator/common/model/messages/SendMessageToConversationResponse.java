package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Message;

import java.util.UUID;

public class SendMessageToConversationResponse extends MessageToWebsocket {
    private final Message message;

    public SendMessageToConversationResponse(UUID id, Message message) {
        super(MessageType.CLIENT_SEND_MESSAGE, id);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
