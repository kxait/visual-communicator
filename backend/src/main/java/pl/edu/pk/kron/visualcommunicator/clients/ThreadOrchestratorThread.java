package pl.edu.pk.kron.visualcommunicator.clients;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.model.ThreadOrchestratorMessage;
import pl.edu.pk.kron.visualcommunicator.common.model.ThreadOrchestratorMessageType;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public class ThreadOrchestratorThread implements Runnable {
    private final Dictionary<UUID, Thread> clientThreads;
    private final MessageBus bus;
    private final Gson gson;
    private final ClientDataProviderAdapter dataProvider;
    private final AuthenticatedUserRegistry userRegistry;

    public ThreadOrchestratorThread(MessageBus bus, ClientDataProviderAdapter dataProvider, AuthenticatedUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
        gson = new Gson();
        this.clientThreads = new Hashtable<>();
        this.bus = bus;
        this.dataProvider = dataProvider;
    }

    @Override
    public void run() {
        while(true) {
            var message = bus.pollByPredicate(m -> m.type() == BusMessageType.MESSAGE_TO_THREAD_ORCHESTRATOR);
            if(message == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // killed by ???
                    break;
                }
                continue;
            }
            var content = gson.fromJson(message.jsonContent(), ThreadOrchestratorMessage.class);
            if(content.type() == ThreadOrchestratorMessageType.DIE) {
                break;
            }

            if(content.type() == ThreadOrchestratorMessageType.CONNECTED) {
                var uuid = content.clientId();
                var thread = new Thread(new ClientThread(bus, uuid, dataProvider, userRegistry));
                thread.start();
                clientThreads.put(uuid, thread);
            }

            if(content.type() == ThreadOrchestratorMessageType.DISCONNECTED) {
                var uuid = content.clientId();
                var thread = clientThreads.get(uuid);
                thread.interrupt();
                clientThreads.remove(uuid);
            }
        }
    }
}
