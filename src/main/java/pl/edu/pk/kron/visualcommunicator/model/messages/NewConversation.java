package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Conversation;

import java.util.UUID;

public class NewConversation extends MessageToClient {
    private final Conversation conversation;

    public NewConversation(Conversation conversation) {
        super(MessageType.SERVER_NEW_CONVERSATION);
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }
}
