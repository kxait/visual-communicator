package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Error;

import java.util.UUID;

public class Err extends MessageToClient {
    private final Error error;

    public Err(Error error) {
        super(MessageType.SERVER_ERR);
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
