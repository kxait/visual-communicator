package pl.edu.pk.kron.visualcommunicator;
import pl.edu.pk.kron.visualcommunicator.websocket.VisualCommunicatorWebsocketServer;

public class Main {
    private static final int PORT = 8887;

    public static void main(String[] args) {


        var server = new VisualCommunicatorWebsocketServer(PORT);
        server.start();
    }
}