package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.MessageRecipient;

import java.util.List;
import java.util.UUID;

public class GetUsersByIdOrPartOfNameResponse extends MessageToWebsocket {
    private final List<MessageRecipient> recipients;

    public GetUsersByIdOrPartOfNameResponse(UUID id, List<MessageRecipient> recipients) {
        super(MessageType.CLIENT_GET_USERS_BY_ID_OR_PART_OF_NAME, id);
        this.recipients = recipients;
    }

    public List<MessageRecipient> getRecipients() {
        return recipients;
    }
}
