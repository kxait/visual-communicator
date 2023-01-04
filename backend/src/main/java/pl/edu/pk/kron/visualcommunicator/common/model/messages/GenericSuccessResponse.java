package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GenericSuccessResponse extends MessageToWebsocket {
    private final boolean success;

    public GenericSuccessResponse(MessageType type, UUID id, boolean success) {
        super(type, id);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
