package pl.edu.pk.kron.visualcommunicator.websocket;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;

public class WebsocketMessageSender implements Runnable {
    private final MessageBus bus;
    private final VisualCommunicatorWebsocketServer server;

    public WebsocketMessageSender(MessageBus bus, VisualCommunicatorWebsocketServer server) {
        this.bus = bus;
        this.server = server;
    }

    @Override
    public void run() {
        while(true) {
            var message = bus.pollByPredicate(m -> m.type() == BusMessageType.MESSAGE_TO_WEBSOCKET);
            if(message == null) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    // killed by ???
                    e.printStackTrace();
                    break;
                }
                continue;
            }
            server.sendToWebsocket(message.jsonContent(), message.recipientId());
        }
        System.out.println("💀 WebsocketMessageSender died");
    }
}
