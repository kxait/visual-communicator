package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

public class NewMessageInConversationResponse extends MessageFromClient {
    public NewMessageInConversationResponse() {
        super(MessageType.SERVER_NEW_MESSAGE);
    }
}
