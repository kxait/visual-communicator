package pl.edu.pk.kron.visualcommunicator.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;

import java.net.InetSocketAddress;
import java.util.*;

class Client {
    private final UUID id;
    private final InetSocketAddress address;
    private boolean isAuthenticated = false;

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

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}

public class VisualCommunicatorWebsocketServer extends WebSocketServer {
    private Dictionary<UUID, Client> clientByClientId;
    private Dictionary<InetSocketAddress, Client> clientBySocketAddress;
    private final MessageBus bus;

    public VisualCommunicatorWebsocketServer(int port, MessageBus bus) {
        super(new InetSocketAddress(port));
        System.out.println("web socket server created at " + port);
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
        var decoded = s;//new String(Base64.getDecoder().decode(s));
        System.out.println("websocket message received: addr=" + webSocket.getRemoteSocketAddress() + ", decoded=" + decoded + ", s=" + s);
        var client = clientBySocketAddress.get(webSocket.getRemoteSocketAddress());
        var id = client.getId();
        var busMessage = new BusMessage(decoded, BusMessageType.MESSAGE_TO_CLIENT_THREAD, id);
        bus.pushOntoBus(busMessage);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("websocket error addr=" + webSocket.getRemoteSocketAddress());

        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("web socket server listening");
    }

    public void sendToWebsocket(String content, UUID clientId) {
        var client = clientByClientId.get(clientId);
        var connection = getConnections()
                .stream()
                .filter(c -> c.getRemoteSocketAddress() == client.getAddress())
                .findFirst()
                .get();

        var encoded = content;//Base64.getEncoder().encodeToString(content.getBytes());

        connection.send(encoded);
    }
}
