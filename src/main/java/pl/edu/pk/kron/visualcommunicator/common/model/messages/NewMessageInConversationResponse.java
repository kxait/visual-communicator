package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

public class NewMessageInConversationResponse extends MessageFromWebsocket {
    public NewMessageInConversationResponse() {
        super(MessageType.SERVER_NEW_MESSAGE);
    }
}
