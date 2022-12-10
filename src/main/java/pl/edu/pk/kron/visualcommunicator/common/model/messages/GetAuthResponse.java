package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.User;

public class GetAuthResponse extends MessageToWebsocket {
    private final User user;

    public GetAuthResponse(User user) {
        super(MessageType.CLIENT_GET_AUTH);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
