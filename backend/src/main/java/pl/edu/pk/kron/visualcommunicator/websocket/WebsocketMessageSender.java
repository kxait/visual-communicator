package pl.edu.pk.kron.visualcommunicator.websocket;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogManager;

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
            try {
                var message = bus.pollByPredicate(m -> m.type() == BusMessageType.MESSAGE_TO_WEBSOCKET);
                if (message == null) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    continue;
                }
                server.sendToWebsocket(message.jsonContent(), message.recipientId());
            }catch(Exception e) {
                e.printStackTrace();
                LogManager.instance().logError("WebsocketMessageSender encountered a severe error: %s", e.getMessage());
            }
        }
        LogManager.instance().logError("WebsocketMessageSender died");
    }
}
