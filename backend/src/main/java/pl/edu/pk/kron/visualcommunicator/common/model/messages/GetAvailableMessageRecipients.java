package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetAvailableMessageRecipients extends MessageFromWebsocket {
    public GetAvailableMessageRecipients(UUID id) {
        super(id, MessageType.CLIENT_GET_AVAILABLE_MESSAGE_RECIPIENTS);
    }
}
