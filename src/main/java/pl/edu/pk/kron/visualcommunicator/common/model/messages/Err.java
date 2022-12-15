package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Error;

import java.util.UUID;

public class Err extends MessageToWebsocket {
    private final Error error;

    public Err(UUID id, Error error) {
        super(MessageType.SERVER_ERR, id);
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
