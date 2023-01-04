package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class WhoAmI extends MessageFromWebsocket {
    public WhoAmI(UUID id) {
        super(id, MessageType.CLIENT_WHO_AM_I);
    }
}
