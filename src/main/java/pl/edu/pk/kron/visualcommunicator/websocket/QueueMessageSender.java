package pl.edu.pk.kron.visualcommunicator.websocket;

import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientConnectedMessage;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientDisconnectedMessage;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientSentMessage;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.WebMsgMediator;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageFromClientCombined;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueMessageSender {
    private final Executor exe;
    private final WebMsgMediator mediator;

    public QueueMessageSender(WebMsgMediator mediator) {
        this.mediator = mediator;
        exe = Executors.newCachedThreadPool();
    }

    public void connectedClient(InetSocketAddress address) {
        mediator.sendMessage(ClientConnectedMessage.class, new ClientConnectedMessage(address));
    }

    public void disconnectedClient(InetSocketAddress address) {
        mediator.sendMessage(ClientDisconnectedMessage.class, new ClientDisconnectedMessage(address));
    }

    public void receivedMessage(MessageFromClientCombined message) {
        mediator.sendMessage(ClientSentMessage.class, new ClientSentMessage(message));
    }
}
