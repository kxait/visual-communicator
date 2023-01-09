package pl.edu.pk.kron.visualcommunicator.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogManager;

import java.net.InetSocketAddress;
import java.util.*;

class Client {
    private final UUID id;
    private final InetSocketAddress address;

    public Client(InetSocketAddress address) {
        this.address = address;
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}

public class WebsocketServer extends WebSocketServer {
    private final Dictionary<UUID, Client> clientByClientId;
    private final Dictionary<InetSocketAddress, Client> clientBySocketAddress;
    private final MessageBus bus;

    public WebsocketServer(int port, MessageBus bus) {
        super(new InetSocketAddress(port));
        LogManager.instance().logInfo("web socket server created at %d",  port);
        this.bus = bus;

        clientByClientId = new Hashtable<>();
        clientBySocketAddress = new Hashtable<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        var client = new Client(webSocket.getRemoteSocketAddress());
        clientByClientId.put(client.getId(), client);
        clientBySocketAddress.put(webSocket.getRemoteSocketAddress(), client);
        var busMessage = new BusMessageGenerator().getClientConnectedMessage(client.getId());
        bus.pushOntoBus(busMessage);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        var client = clientBySocketAddress.get(webSocket.getRemoteSocketAddress());
        clientByClientId.remove(client.getId());
        clientBySocketAddress.remove(webSocket.getRemoteSocketAddress());
        var busMessage = new BusMessageGenerator().getClientDisconnectedMessage(client.getId());
        bus.pushOntoBus(busMessage);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        var client = clientBySocketAddress.get(webSocket.getRemoteSocketAddress());
        var id = client.getId();
        var busMessage = new BusMessage(s, BusMessageType.MESSAGE_TO_CLIENT_THREAD, id);
        bus.pushOntoBus(busMessage);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        LogManager.instance().logInfo("web socket server listening");
    }

    public void sendToWebsocket(String content, UUID clientId) {
        var client = clientByClientId.get(clientId);
        var connection = getConnections()
                .stream()
                .filter(c -> c.getRemoteSocketAddress() == client.getAddress())
                .findFirst()
                .orElse(null);

        if(connection == null)
            return;

        if(content.equals("disconnect")) {
            connection.close();
            return;
        }

        connection.send(content);
    }
}
