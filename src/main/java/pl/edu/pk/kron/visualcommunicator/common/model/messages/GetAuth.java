package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Token;

public class GetAuth extends MessageFromWebsocket {
    private final Token token;

    public GetAuth(Token token) {
        super(MessageType.CLIENT_GET_AUTH);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
