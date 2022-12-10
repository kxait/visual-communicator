package pl.edu.pk.kron.visualcommunicator.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.messages.GetAuthResponse;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageToClientCombined;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.Hashtable;
import java.util.UUID;

class Client {
    private final InetSocketAddress address;
    private UUID id = null;

    public Client(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

public class VisualCommunicatorWebsocketServer extends WebSocketServer {
    private Hashtable<InetSocketAddress, Client> clients;
    private final QueueMessageSender sender;
    private MessageParser parser;
    private MessageSerializer serializer;

    public VisualCommunicatorWebsocketServer(int port, QueueMessageSubscriber subscriber, QueueMessageSender sender) {
        super(new InetSocketAddress(port));

        System.out.println("web socket server created at " + port);

        this.sender = sender;
        subscriber.bind(this::onMessageToClient, this::onDisconectClient);
        clients = new Hashtable<>();
        parser = new MessageParser();
        serializer = new MessageSerializer();
    }

    private void onDisconectClient(InetSocketAddress address) {
        getConnections()
                .stream()
                .filter(conn -> conn.getRemoteSocketAddress() == address)
                .forEach(WebSocket::close);
    }

    private void onMessageToClient(MessageToClientCombined msg) {
        var address = msg.getMetadata().getToAddress();

        var user = clients.get(address);

        if(msg.getMessage().getType() == MessageType.CLIENT_GET_AUTH) {
            var userId = ((GetAuthResponse)(msg.getMessage())).getUser().getId();
            System.out.println("User with id " + userId + " authenticated on address " + address);
            user.setId(userId);
        }

        var receiveSock = getConnections()
                .stream()
                .filter(conn -> conn.getRemoteSocketAddress() == address)
                .findFirst()
                .get();

        var serialized = serializer.serializeB64(msg);
        receiveSock.send(serialized);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("web socket client connected " + webSocket.getLocalSocketAddress() + ", " + webSocket.getRemoteSocketAddress());
        var client = new Client(webSocket.getRemoteSocketAddress());
        clients.put(webSocket.getRemoteSocketAddress(), client);
        sender.connectedClient(webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("web socket client disconnected i=" + i + ", s=" + s + ", b=" + b + ", addr=" + webSocket.getRemoteSocketAddress());
        clients.remove(webSocket.getRemoteSocketAddress());
        sender.disconnectedClient(webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        var decoded = new String(Base64.getDecoder().decode(s));
        System.out.println("websocket message received: addr=" + webSocket.getRemoteSocketAddress() + ", decoded=" + decoded + ", s=" + s);

        var sourceClient = clients.get(webSocket.getRemoteSocketAddress());

        var parsed = parser.parseMessageFromClient(decoded, sourceClient.getId(), webSocket.getRemoteSocketAddress());
        if(parsed == null || (sourceClient.getId() == null && parsed.getMessage().getType() != MessageType.CLIENT_GET_AUTH)) {
            webSocket.send(Base64.getEncoder().encodeToString("error".getBytes()));
            return;
        };

        webSocket.send(Base64.getEncoder().encodeToString(parsed.getMetadata().getId().toString().getBytes()));
        sender.receivedMessage(parsed);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("websocket error addr=" + webSocket.getRemoteSocketAddress());

        var id = clients.get(webSocket.getRemoteSocketAddress()).getId();

        // TODO:
        //sender.sendError(null, id, e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("web socket server listening");
        // ???
    }
}
