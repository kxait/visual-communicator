package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Token;

import java.util.UUID;

public class GetAuthTokenResponse extends MessageToWebsocket {
    private final Token token;
    private final UUID userId;
    private final String name;

    public GetAuthTokenResponse(UUID id, Token token, UUID userId, String name) {
        super(MessageType.CLIENT_GET_AUTH_TOKEN, id);
        this.token = token;
        this.userId = userId;
        this.name = name;
    }

    public Token getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
