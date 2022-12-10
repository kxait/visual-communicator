package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Conversation;

import java.util.List;

public class GetConversationsResponse extends MessageToWebsocket {

    private final List<Conversation> conversations;

    public GetConversationsResponse(List<Conversation> conversations) {
        super(MessageType.CLIENT_GET_CONVERSATIONS);
        this.conversations = conversations;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
}
