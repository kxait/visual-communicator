package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetUsernameOfUserId extends MessageFromWebsocket {
    private final UUID userId;

    public GetUsernameOfUserId(UUID id, UUID userId) {
        super(id, MessageType.CLIENT_GET_USERNAME_OF_USERID);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
