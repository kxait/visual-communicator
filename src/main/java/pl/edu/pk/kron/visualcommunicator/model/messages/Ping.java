package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Ping extends MessageToClient {
    private final Date ping;
    public Ping() {
        super(MessageType.SERVER_PING);
        ping = Date.from(Instant.now());
    }

    public Date getPing() {
        return ping;
    }
}
