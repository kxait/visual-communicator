package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetUsernameOfUserIdResponse extends MessageToWebsocket {
    private final String username;
    public GetUsernameOfUserIdResponse(UUID id, String username) {
        super(MessageType.CLIENT_GET_USERNAME_OF_USERID, id);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
