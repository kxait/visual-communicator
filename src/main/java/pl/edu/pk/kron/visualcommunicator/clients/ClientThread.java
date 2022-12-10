package pl.edu.pk.kron.visualcommunicator.clients;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;

import java.util.UUID;

public class ClientThread implements Runnable {
    private final MessageBus bus;
    private final UUID clientId;

    public ClientThread(MessageBus bus, UUID clientId) {
        this.bus = bus;
        this.clientId = clientId;
        System.out.println("client thread for " + clientId.toString() + " started");
    }

    @Override
    public void run() {
        while(true) {
            var message = bus.pollByPredicate(m -> m.type() == BusMessageType.MESSAGE_TO_CLIENT_THREAD && m.recipientId().equals(clientId));
            if(message == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                    // killed by orchestrator
                    System.out.println("client thread for " + clientId + " killed");
                    break;
                }
                continue;
            }

            var busMessage = new BusMessage(message.jsonContent(), BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
            bus.pushOntoBus(busMessage);
        }
    }
}
