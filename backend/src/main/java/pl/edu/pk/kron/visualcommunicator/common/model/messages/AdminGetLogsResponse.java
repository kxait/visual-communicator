package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import pl.edu.pk.kron.visualcommunicator.common.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Log;

import java.util.List;
import java.util.UUID;

public class AdminGetLogsResponse extends MessageToWebsocket {
    private final List<Log> logs;

    public AdminGetLogsResponse(UUID id, List<Log> logs) {
        super(MessageType.CLIENT_ADMIN_GET_LOGS, id);
        this.logs = logs;
    }

    public List<Log> getLogs() {
        return logs;
    }
}
