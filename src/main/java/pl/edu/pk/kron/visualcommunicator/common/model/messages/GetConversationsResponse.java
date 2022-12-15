package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Conversation;

import java.util.List;
import java.util.UUID;

public class GetConversationsResponse extends MessageToWebsocket {

    private final List<Conversation> conversations;

    public GetConversationsResponse(UUID id, List<Conversation> conversations) {
        super(MessageType.CLIENT_GET_CONVERSATIONS, id);
        this.conversations = conversations;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
}
