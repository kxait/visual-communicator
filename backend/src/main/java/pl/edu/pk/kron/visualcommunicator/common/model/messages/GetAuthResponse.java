package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetAuthResponse extends MessageToWebsocket {
    private final UUID userId;
    private final String name;

    public GetAuthResponse(UUID id, UUID userId, String name) {
        super(MessageType.CLIENT_GET_AUTH, id);
        this.userId = userId;
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
