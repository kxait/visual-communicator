package pl.edu.pk.kron.visualcommunicator.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pl.edu.pk.kron.visualcommunicator.model.MessageType;
import pl.edu.pk.kron.visualcommunicator.model.messages.GetAuthResponse;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageToClientCombined;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.UUID;

public class VisualCommunicatorWebsocketServer extends WebSocketServer {

    public VisualCommunicatorWebsocketServer(int port) {
        super(new InetSocketAddress(port));

        System.out.println("web socket server created at " + port);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("web socket client connected " + webSocket.getLocalSocketAddress() + ", " + webSocket.getRemoteSocketAddress());

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("web socket client disconnected i=" + i + ", s=" + s + ", b=" + b + ", addr=" + webSocket.getRemoteSocketAddress());

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        var decoded = new String(Base64.getDecoder().decode(s));
        System.out.println("websocket message received: addr=" + webSocket.getRemoteSocketAddress() + ", decoded=" + decoded + ", s=" + s);

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
}
