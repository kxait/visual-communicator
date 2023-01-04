package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetAuth extends MessageFromWebsocket {
    private final String token;

    public GetAuth(UUID id, String token) {
        super(id, MessageType.CLIENT_GET_AUTH);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
