package pl.edu.pk.kron.visualcommunicator;

import pl.edu.pk.kron.visualcommunicator.business.ClientOracle;
import pl.edu.pk.kron.visualcommunicator.business.ClientQueueMessageSender;
import pl.edu.pk.kron.visualcommunicator.business.ClientQueueMessageSubscriber;
import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.*;
import pl.edu.pk.kron.visualcommunicator.websocket.QueueMessageSender;
import pl.edu.pk.kron.visualcommunicator.websocket.QueueMessageSubscriber;
import pl.edu.pk.kron.visualcommunicator.websocket.VisualCommunicatorWebsocketServer;

public class Main {
    private static final int PORT = 8887;

    public static void main(String[] args) {

        var mediator = new WebMsgMediator();

        var webMsgSender = new QueueMessageSender(mediator);
        var webMsgSubscriber = new QueueMessageSubscriber();

        var clientMsgSender = new ClientQueueMessageSender(mediator);
        var clientMsgSubscriber = new ClientQueueMessageSubscriber();

        var ignored = new ClientOracle(clientMsgSender, clientMsgSubscriber);

        mediator.bind(ClientConnectedMessage.class, clientMsgSubscriber::clientConnected);
        mediator.bind(ClientDisconnectedMessage.class, clientMsgSubscriber::clientDisconnected);
        mediator.bind(ClientSentMessage.class, clientMsgSubscriber::clientMessageReceived);
        mediator.bind(SendMessageToClient.class, webMsgSubscriber::messageToClient);
        mediator.bind(DisconnectClient.class, webMsgSubscriber::disconnectClient);

        var server = new VisualCommunicatorWebsocketServer(PORT, webMsgSubscriber, webMsgSender);
        server.start();
    }
}