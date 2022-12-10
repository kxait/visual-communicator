package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

public class NewConversationResponse extends MessageFromClient {
    public NewConversationResponse() {
        super(MessageType.SERVER_NEW_CONVERSATION);
    }
}
