package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.User;

import java.util.List;
import java.util.UUID;

public class AdminGetAllUsersResponse extends MessageToWebsocket {
    private final List<User> users;

    public AdminGetAllUsersResponse(UUID id, List<User> users) {
        super(MessageType.CLIENT_ADMIN_GET_ALL_USERS, id);
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
