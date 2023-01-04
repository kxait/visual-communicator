package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminRenameUser extends MessageFromWebsocket {
    private final UUID userToRename;
    private final String newName;

    public AdminRenameUser(UUID id, UUID userToRename, String newName) {
        super(id, MessageType.CLIENT_ADMIN_RENAME_USER);
        this.userToRename = userToRename;
        this.newName = newName;
    }

    public UUID getUserToRename() {
        return userToRename;
    }

    public String getNewName() {
        return newName;
    }
}
