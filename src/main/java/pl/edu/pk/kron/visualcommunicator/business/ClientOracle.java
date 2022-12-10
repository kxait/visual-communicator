package pl.edu.pk.kron.visualcommunicator.business;

import pl.edu.pk.kron.visualcommunicator.model.messages.*;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientOracle {
    private final Hashtable<InetSocketAddress, ClientThread> threads;
    private final Executor exe;

    private final ClientQueueMessageSender sender;

    public ClientOracle(ClientQueueMessageSender sender, ClientQueueMessageSubscriber subscriber) {
        threads = new Hashtable<>();
        exe = Executors.newCachedThreadPool();
        this.sender = sender;

        subscriber.bind(this::clientConnected, this::clientDisconnected, this::clientMessageReceived);
    }

    public void clientConnected(InetSocketAddress addr) {
        var thread = new ClientThread(this, addr);
        threads.put(addr, thread);
        exe.execute(thread);
    }
    public void clientDisconnected(InetSocketAddress addr) {
        var thread = threads.get(addr);
        thread.interrupt();
        threads.remove(addr);
    }
    public void clientMessageReceived(MessageFromClientCombined msg) {
        var message = msg.getMessage();
        var metadata = msg.getMetadata();

        var thread = threads.get(metadata.getFromAddress());
        switch(message.getType()) {
            case CLIENT_SEND_MESSAGE -> thread.sendMessageToConversation((SendMessageToConversation) message);
            case CLIENT_GET_MESSAGES -> thread.getMessages((GetMessages) message);
            case CLIENT_GET_AUTH -> thread.getAuth((GetAuth) message);
            case CLIENT_GET_CONVERSATIONS -> thread.getConversations((GetConversations) message);
            case CLIENT_CREATE_NEW_CONVERSATION -> thread.newConversation((CreateConversation) message);
            case SERVER_PING -> thread.pong((PingResponse) message);
            default -> throw new RuntimeException("message type " + message.getType().toString() + " not handled by thread");
        }
    }

    public void sendMessageToClient(MessageToClientCombined message) {
        sender.sendMessageToClient(message);
    }
}
