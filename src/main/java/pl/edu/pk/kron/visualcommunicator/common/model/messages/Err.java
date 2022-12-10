package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Error;

public class Err extends MessageToWebsocket {
    private final Error error;

    public Err(Error error) {
        super(MessageType.SERVER_ERR);
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
