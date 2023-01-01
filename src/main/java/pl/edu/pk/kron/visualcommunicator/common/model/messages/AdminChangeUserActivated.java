package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminChangeUserActivated extends MessageFromWebsocket {
    private final UUID userId;
    private final boolean activated;

    public AdminChangeUserActivated(UUID id, UUID userId, boolean activated) {
        super(id, MessageType.CLIENT_ADMIN_CHANGE_USER_ACTIVATED);
        this.userId = userId;
        this.activated = activated;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isActivated() {
        return activated;
    }
}
