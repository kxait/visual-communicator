package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

public class GetConversations extends MessageFromClient {

    public GetConversations() {
        super(MessageType.CLIENT_GET_CONVERSATIONS);
    }
}
