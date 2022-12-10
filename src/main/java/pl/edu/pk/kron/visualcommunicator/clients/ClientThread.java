package pl.edu.pk.kron.visualcommunicator.clients;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.User;
import pl.edu.pk.kron.visualcommunicator.common.model.messages.*;

import java.util.UUID;
import java.util.function.Function;

public class ClientThread implements Runnable {
    private final MessageBus bus;
    private final UUID clientId;
    private final Gson gson;
    private User user;

    public ClientThread(MessageBus bus, UUID clientId) {
        this.bus = bus;
        this.clientId = clientId;
        System.out.println("client thread for " + clientId.toString() + " started");
        gson = new Gson();
    }

    @Override
    public void run() {
        while(true) {
            var message = bus.pollByPredicate(m -> m.type() == BusMessageType.MESSAGE_TO_CLIENT_THREAD && m.recipientId().equals(clientId));
            if(message == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                    // killed by orchestrator
                    System.out.println("client thread for " + clientId + " killed");
                    break;
                }
                continue;
            }

            var m = message.jsonContent();
            var messageType = gson.fromJson(m, MessageFromWebsocket.class).getType();
            var response = switch(messageType) {
                case CLIENT_GET_AUTH -> getAuth(gson.fromJson(m, GetAuth.class));
                case CLIENT_GET_CONVERSATIONS -> getConversations(gson.fromJson(m, GetConversations.class));
                case CLIENT_GET_MESSAGES -> getMessages(gson.fromJson(m, GetMessages.class));
                case CLIENT_SEND_MESSAGE -> sendMessageToConversation(gson.fromJson(m, SendMessageToConversation.class));
                case CLIENT_CREATE_NEW_CONVERSATION -> createConversation(gson.fromJson(m, CreateConversation.class));
                default -> throw new IllegalStateException("Unexpected value: " + messageType);
            };

            var busMessage = new BusMessage(gson.toJson(response), BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
            bus.pushOntoBus(busMessage);
        }
    }

    private GetAuthResponse getAuth(GetAuth getAuth) {
        user = new User(UUID.randomUUID(), UUID.randomUUID().toString());
        return new GetAuthResponse(user);
    }

    private GetConversationsResponse getConversations(GetConversations getConversations) {
        return null;
    }

    private GetMessagesResponse getMessages(GetMessages getMessages) {
        return null;
    }

    // produces NewMessageInConversation for recipients
    private SendMessageToConversationResponse sendMessageToConversation(SendMessageToConversation sendMessageToConversation) {
        return null;
    }

    // produces NewConversation for recipients
    private CreateConversationResponse createConversation(CreateConversation createConversation) {
        return null;
    }
}
