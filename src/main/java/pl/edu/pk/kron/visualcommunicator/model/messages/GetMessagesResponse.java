package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Message;

import java.util.List;

public class GetMessagesResponse extends MessageToClient {
    private final List<Message> messages;

    public GetMessagesResponse(List<Message> messages) {
        super(MessageType.CLIENT_GET_MESSAGES);
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
