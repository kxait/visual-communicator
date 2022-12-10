package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Conversation;

public class CreateConversationResponse extends MessageToWebsocket {
    private final Conversation conversation;

    public CreateConversationResponse(Conversation conversation) {
        super(MessageType.CLIENT_CREATE_NEW_CONVERSATION);
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }
}
