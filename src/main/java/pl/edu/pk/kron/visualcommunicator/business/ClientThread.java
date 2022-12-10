package pl.edu.pk.kron.visualcommunicator.business;

import pl.edu.pk.kron.visualcommunicator.infrastructure.mediator.ClientSentMessage;
import pl.edu.pk.kron.visualcommunicator.model.MessageToConversation;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.Error;
import pl.edu.pk.kron.visualcommunicator.model.message_contents.User;
import pl.edu.pk.kron.visualcommunicator.model.messages.*;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ClientThread implements Runnable {
    private final ClientOracle mediator;

    private boolean isInterrupted = false;

    private Ping lastPing;

    private UUID id;

    private final InetSocketAddress address;

    public ClientThread(ClientOracle mediator, InetSocketAddress address) {
        this.mediator = mediator;
        this.address = address;
    }

    private void parseMessage() {

    }

    @Override
    public void run() {
        while(!isInterrupted) {
            try {
                Thread.sleep(10);
            }catch(InterruptedException ignored){}
            if(1 != 1) break;
        }
        System.out.println("goodbye from " + id);
    }

    public void newMessageInConversation(NewMessageInConversation message) {
        System.out.println("new message in conversation");
        sendAck();
    }

    public void newConversation(CreateConversation message) {
        System.out.println("new conversation");
        sendAck();

    }

    public void getMessages(GetMessages get) {
        System.out.println("get messages");
        sendAck();

    }

    public void getConversations(GetConversations get) {
        System.out.println("get conversations");
        sendAck();
    }

    public void getAuth(GetAuth message) {
        System.out.println("get auth");

        if(message.getToken().getValue().equals("1")) {
            this.id = UUID.fromString("dac3aa44-321b-4c16-980c-4a0ed9970642");
            var response = produceResponse(message.getId(), new GetAuthResponse(new User(this.id, "userino")));
            mediator.sendMessageToClient(response);
        }else{
            err(message.getId());
        }
    }

    public void sendMessageToConversation(SendMessageToConversation message) {
        System.out.println("send message to conversation");
        sendAck();

    }

    public void notifyNewConversation(NewConversation conversation) {
        System.out.println("notify new conversation");
        sendAck();

    }

    public void notifyNewMessage(NewMessageInConversation message) {
        System.out.println("notify new message");
        sendAck();

    }

    public void interrupt(){
        isInterrupted = true;
    }

    public void pong(PingResponse response) {
        var lastPingTime = lastPing.getPing();
        var pongTime = response.getPong();

        var diff = pongTime.toInstant().getNano() - lastPingTime.toInstant().getNano();
        System.out.println(diff + " nanos");
    }

    public void sendErrorResponse(UUID messageId, String content) {
        var message = new Err(new Error(true, content));
        var metadata = new MessageToClientMetadata(messageId, address);
        var comb = new MessageToClientCombined(message, metadata);
        mediator.sendMessageToClient(comb);
    }

    public void err(UUID id) {
        sendErrorResponse(id, "oops!");
    }

    private void sendAck() {
        var message = new Ping();
        lastPing = message;
        var metadata = new MessageToClientMetadata(UUID.randomUUID(), address);
        var comb = new MessageToClientCombined(message, metadata);
        mediator.sendMessageToClient(comb);
    }

    private MessageToClientCombined produceResponse(UUID messageId, MessageToClient message) {
        var metadata = new MessageToClientMetadata(messageId, address);
        var comb = new MessageToClientCombined(message, metadata);
        return comb;
    }

    public UUID getId() {
        return id;
    }
}
