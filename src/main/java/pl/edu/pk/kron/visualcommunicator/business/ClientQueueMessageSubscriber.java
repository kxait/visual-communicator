package pl.edu.pk.kron.visualcommunicator.business;

import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientConnectedMessage;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientDisconnectedMessage;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientSentMessage;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageFromClientCombined;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class ClientQueueMessageSubscriber {
    private Consumer<InetSocketAddress> onClientConnected;
    private Consumer<InetSocketAddress> onClientDisconnected;
    private Consumer<MessageFromClientCombined> onClientSentMessage;

    public void clientConnected(ClientConnectedMessage addr) {
        onClientConnected.accept(addr.address());
    }
    public void clientDisconnected(ClientDisconnectedMessage addr) {
        onClientDisconnected.accept(addr.address());
    }
    public void clientMessageReceived(ClientSentMessage msg) {
        onClientSentMessage.accept(msg.message());
    }

    public void bind(Consumer<InetSocketAddress> onClientConnected, Consumer<InetSocketAddress> onClientDisconnected, Consumer<MessageFromClientCombined> onClientSentMessage) {
        this.onClientConnected = onClientConnected;
        this.onClientDisconnected = onClientDisconnected;
        this.onClientSentMessage = onClientSentMessage;
    }

}
