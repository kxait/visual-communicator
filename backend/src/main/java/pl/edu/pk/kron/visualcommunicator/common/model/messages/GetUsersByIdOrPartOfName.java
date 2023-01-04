package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetUsersByIdOrPartOfName extends MessageFromWebsocket {
    private final String input;

    public GetUsersByIdOrPartOfName(UUID id, String input) {
        super(id, MessageType.CLIENT_GET_USERS_BY_ID_OR_PART_OF_NAME);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
