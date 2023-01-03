package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class GetProfileDataResponse extends MessageToWebsocket {
    private final String profileData;

    public GetProfileDataResponse(UUID id, String profileData) {
        super(MessageType.CLIENT_GET_PROFILE_DATA, id);
        this.profileData = profileData;
    }

    public String getProfileData() {
        return profileData;
    }
}
