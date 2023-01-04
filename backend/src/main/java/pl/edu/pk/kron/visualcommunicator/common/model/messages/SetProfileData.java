package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class SetProfileData extends MessageFromWebsocket {
    private final String profileData;


    public SetProfileData(UUID id, String profileData) {
        super(id, MessageType.CLIENT_SET_PROFILE_DATA);
        this.profileData = profileData;
    }

    public String getProfileData() {
        return profileData;
    }
}
