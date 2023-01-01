package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class RenameMe extends MessageFromWebsocket {
    private final String newName;

    public RenameMe(UUID id, String newName) {
        super(id, MessageType.CLIENT_RENAME_ME);
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
