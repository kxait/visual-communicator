package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Token;

public class GetAuth extends MessageFromClient {
    private final Token token;

    public GetAuth(Token token) {
        super(MessageType.CLIENT_GET_AUTH);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
