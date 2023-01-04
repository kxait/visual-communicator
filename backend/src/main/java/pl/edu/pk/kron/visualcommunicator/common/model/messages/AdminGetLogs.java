package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;

import java.util.UUID;

public class AdminGetLogs extends MessageFromWebsocket {
    private final long count;

    public AdminGetLogs(UUID id, long count) {
        super(id, MessageType.CLIENT_ADMIN_GET_LOGS);
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}
