package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminCreateNewUserResponse extends MessageToWebsocket {
    private final UUID userId;

    public AdminCreateNewUserResponse(UUID id, UUID userId) {
        super(MessageType.CLIENT_ADMIN_CREATE_NEW_USER, id);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
