package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Token;

public class GetAuth extends MessageFromWebsocket {
    private final String token;

    public GetAuth(String token) {
        super(MessageType.CLIENT_GET_AUTH);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
