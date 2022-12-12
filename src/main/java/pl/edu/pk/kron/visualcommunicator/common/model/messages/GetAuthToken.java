package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

public class GetAuthToken extends MessageFromWebsocket {
    private final String userName;
    private final String passwordHash;

    public GetAuthToken(String userName, String passwordHash) {
        super(MessageType.CLIENT_GET_AUTH_TOKEN);
        this.userName = userName;
        this.passwordHash = passwordHash;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
