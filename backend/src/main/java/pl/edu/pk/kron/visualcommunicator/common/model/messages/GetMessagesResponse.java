package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Message;

import java.util.List;
import java.util.UUID;

public class GetMessagesResponse extends MessageToWebsocket {
    private final List<Message> messages;

    public GetMessagesResponse(UUID id, List<Message> messages) {
        super(MessageType.CLIENT_GET_MESSAGES, id);
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
