package pl.edu.pk.kron.visualcommunicator.websocket;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.model.ThreadOrchestratorMessage;
import pl.edu.pk.kron.visualcommunicator.common.model.ThreadOrchestratorMessageType;

import java.util.UUID;

public class BusMessageGenerator {
    private final Gson gson;

    public BusMessageGenerator() {
        gson = new Gson();
    }

    public BusMessage getClientConnectedMessage(UUID id) {
        var threadOrchestratorMessage = new ThreadOrchestratorMessage(ThreadOrchestratorMessageType.CONNECTED, id);
        return new BusMessage(gson.toJson(threadOrchestratorMessage), BusMessageType.MESSAGE_TO_THREAD_ORCHESTRATOR, null);
    }


    public BusMessage getClientDisconnectedMessage(UUID id) {
        var threadOrchestratorMessage = new ThreadOrchestratorMessage(ThreadOrchestratorMessageType.DISCONNECTED, id);
        return new BusMessage(gson.toJson(threadOrchestratorMessage), BusMessageType.MESSAGE_TO_THREAD_ORCHESTRATOR, null);
    }
}
