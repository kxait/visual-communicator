package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

public class NewConversationResponse extends MessageFromWebsocket {
    public NewConversationResponse() {
        super(MessageType.SERVER_NEW_CONVERSATION);
    }
}
