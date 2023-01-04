package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetProfileData extends MessageFromWebsocket {
    public GetProfileData(UUID id) {
        super(id, MessageType.CLIENT_GET_PROFILE_DATA);
    }
}
