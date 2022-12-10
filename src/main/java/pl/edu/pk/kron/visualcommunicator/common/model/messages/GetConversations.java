package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

public class GetConversations extends MessageFromWebsocket {

    public GetConversations() {
        super(MessageType.CLIENT_GET_CONVERSATIONS);
    }
}
