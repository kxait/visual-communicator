package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetAuthToken extends MessageFromWebsocket {
    private final String userName;
    private final String passwordHash;
    private final String salt;

    public GetAuthToken(UUID id, String userName, String passwordHash, String salt) {
        super(id, MessageType.CLIENT_GET_AUTH_TOKEN);
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }
}
