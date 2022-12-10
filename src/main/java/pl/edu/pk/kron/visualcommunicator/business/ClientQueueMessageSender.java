package pl.edu.pk.kron.visualcommunicator.business;

import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.DisconnectClient;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.SendMessageToClient;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.WebMsgMediator;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageToClientCombined;

import java.net.InetSocketAddress;

public class ClientQueueMessageSender {
    private final WebMsgMediator mediator;

    public ClientQueueMessageSender(WebMsgMediator mediator) {
        this.mediator = mediator;
    }

    public void disconnectClient(InetSocketAddress address) {
        mediator.sendMessage(DisconnectClient.class, new DisconnectClient(address));
    }

    public void sendMessageToClient(MessageToClientCombined message) {
        mediator.sendMessage(SendMessageToClient.class, new SendMessageToClient(message));
    }
}
