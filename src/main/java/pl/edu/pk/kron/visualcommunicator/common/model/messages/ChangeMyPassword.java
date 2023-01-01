package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class ChangeMyPassword extends MessageFromWebsocket {
    private final String currentPasswordHash;
    private final String salt;
    private final String newPasswordHash;

    public ChangeMyPassword(UUID id, String currentPasswordHash, String salt, String newPasswordHash) {
        super(id, MessageType.CLIENT_CHANGE_MY_PASSWORD);
        this.currentPasswordHash = currentPasswordHash;
        this.salt = salt;
        this.newPasswordHash = newPasswordHash;
    }

    public String getCurrentPasswordHash() {
        return currentPasswordHash;
    }

    public String getSalt() {
        return salt;
    }

    public String getNewPasswordHash() {
        return newPasswordHash;
    }
}
