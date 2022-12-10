package pl.edu.pk.kron.visualcommunicator.websocket;

import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.DisconnectClient;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.SendMessageToClient;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageToClientCombined;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class QueueMessageSubscriber {
    private Consumer<MessageToClientCombined> onSend;
    private Consumer<InetSocketAddress> onDisconnect;

    public void bind(Consumer<MessageToClientCombined> onSend, Consumer<InetSocketAddress> onDisconnect) {
        this.onSend = onSend;
        this.onDisconnect = onDisconnect;
    }

    public void messageToClient(SendMessageToClient message) {
        onSend.accept(message.message());
    }

    public void disconnectClient(DisconnectClient client) {
        onDisconnect.accept(client.address());
    }
}
