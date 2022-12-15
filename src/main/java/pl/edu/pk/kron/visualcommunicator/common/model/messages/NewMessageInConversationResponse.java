package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class NewMessageInConversationResponse extends MessageFromWebsocket {
    public NewMessageInConversationResponse(UUID id) {
        super(id, MessageType.SERVER_NEW_MESSAGE);
    }
}
