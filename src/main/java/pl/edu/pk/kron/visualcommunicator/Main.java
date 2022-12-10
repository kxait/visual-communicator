package pl.edu.pk.kron.visualcommunicator;
import pl.edu.pk.kron.visualcommunicator.clients.ThreadOrchestratorThread;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.websocket.VisualCommunicatorWebsocketServer;
import pl.edu.pk.kron.visualcommunicator.websocket.WebsocketMessageSender;

import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8887;

    public static void main(String[] args) {
        var exe = Executors.newCachedThreadPool();

        var bus = new MessageBus();

        var threadOrchestrator = new ThreadOrchestratorThread(bus);
        exe.submit(threadOrchestrator);

        var server = new VisualCommunicatorWebsocketServer(PORT, bus);

        var websocketSender = new WebsocketMessageSender(bus, server);
        exe.submit(websocketSender);

        server.start();
    }
}