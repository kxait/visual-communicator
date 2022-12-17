package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.MessageRecipient;

import java.util.List;
import java.util.UUID;

public class GetAvailableMessageRecipientsResponse extends MessageToWebsocket {
    private final List<MessageRecipient> availableRecipients;

    public GetAvailableMessageRecipientsResponse(UUID id, List<MessageRecipient> availableRecipients) {
        super(MessageType.CLIENT_GET_AVAILABLE_MESSAGE_RECIPIENTS, id);
        this.availableRecipients = availableRecipients;
    }

    public List<MessageRecipient> getAvailableRecipients() {
        return availableRecipients;
    }
}
