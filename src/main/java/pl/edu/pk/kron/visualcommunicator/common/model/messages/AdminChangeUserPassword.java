package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminChangeUserPassword extends MessageFromWebsocket {
    private final UUID userId;
    private final String newPasswordHash;

    public AdminChangeUserPassword(UUID id, UUID userId, String newPasswordHash) {
        super(id, MessageType.CLIENT_ADMIN_CHANGE_USERS_PASSWORD);
        this.userId = userId;
        this.newPasswordHash = newPasswordHash;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getNewPasswordHash() {
        return newPasswordHash;
    }
}
