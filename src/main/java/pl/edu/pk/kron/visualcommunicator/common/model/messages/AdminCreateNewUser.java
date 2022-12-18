package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminCreateNewUser extends MessageFromWebsocket {
    private final String username;
    private final String password;
    private final boolean isAdmin;

    public AdminCreateNewUser(UUID id, String username, String password, boolean isAdmin) {
        super(id, MessageType.CLIENT_ADMIN_CREATE_NEW_USER);
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
