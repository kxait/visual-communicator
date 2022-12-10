package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;

import java.util.Date;

public class PingResponse extends MessageFromClient {

    private final Date pong;

    public PingResponse(Date pong) {
        super(MessageType.SERVER_PING);
        this.pong = pong;
    }

    public Date getPong() {
        return pong;
    }
}
