package pl.edu.pk.kron.visualcommunicator;
import pl.edu.pk.kron.visualcommunicator.clients.AuthenticatedUserRegistry;
import pl.edu.pk.kron.visualcommunicator.clients.ClientDataProviderAdapter;
import pl.edu.pk.kron.visualcommunicator.clients.ThreadOrchestratorThread;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.data_access.MockClientDataProvider;
import pl.edu.pk.kron.visualcommunicator.websocket.VisualCommunicatorWebsocketServer;
import pl.edu.pk.kron.visualcommunicator.websocket.WebsocketMessageSender;

import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8887;

    public static void main(String[] args) {
        var exe = Executors.newCachedThreadPool();

        var bus = new MessageBus();

        var dataProvider = new MockClientDataProvider();
        var providerAdapter = new ClientDataProviderAdapter(dataProvider);

        var userRegistry = new AuthenticatedUserRegistry();

        var threadOrchestrator = new ThreadOrchestratorThread(bus, providerAdapter, userRegistry);
        exe.submit(threadOrchestrator);

        var karol = new VisualCommunicatorWebsocketServer(PORT, bus);

        var websocketSender = new WebsocketMessageSender(bus, karol);
        exe.submit(websocketSender);

        karol.start();
    }
}