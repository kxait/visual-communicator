package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminGetAllUsers extends MessageFromWebsocket {
    public AdminGetAllUsers(UUID id) {
        super(id, MessageType.CLIENT_ADMIN_GET_ALL_USERS);
    }
}
