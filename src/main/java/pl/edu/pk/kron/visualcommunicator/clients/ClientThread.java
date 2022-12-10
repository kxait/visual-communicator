package pl.edu.pk.kron.visualcommunicator.clients;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.User;
import pl.edu.pk.kron.visualcommunicator.common.model.messages.*;

import java.util.UUID;

public class ClientThread implements Runnable {
    private final MessageBus bus;
    private final UUID clientId;
    private final Gson gson;
    private User user;
    private final ClientDataProviderAdapter dataProvider;

    public ClientThread(MessageBus bus, UUID clientId, ClientDataProviderAdapter dataProvider) {
        this.bus = bus;
        this.clientId = clientId;
        this.dataProvider = dataProvider;
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
                case SERVER_NEW_CONVERSATION -> null;
                case SERVER_NEW_MESSAGE -> null;
                default -> throw new IllegalStateException("Unexpected value: " + messageType);
            };

            var busMessage = new BusMessage(gson.toJson(response), BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
            bus.pushOntoBus(busMessage);
        }
    }

    private GetAuthResponse getAuth(GetAuth getAuth) {
        var user = dataProvider.getAuthByToken(getAuth.getToken().getValue());
        if(user != null) {
            this.user = user;
            return new GetAuthResponse(user);
        }
        return null;
    }

    private GetConversationsResponse getConversations(GetConversations getConversations) {
        if(user == null) return null;
        var conversations = dataProvider.getConversationsByUserId(user.id());
        return new GetConversationsResponse(conversations);
    }

    private GetMessagesResponse getMessages(GetMessages getMessages) {
        if(user == null) return null;
        var messages = dataProvider.getMessagesByConversationId(getMessages.getConversationId(), user.id());
        return new GetMessagesResponse(messages);
    }

    // produces NewMessageInConversation for recipients
    private SendMessageToConversationResponse sendMessageToConversation(SendMessageToConversation sendMessageToConversation) {
        if(user == null) return null;

        var message = dataProvider.newMessageInConversation(sendMessageToConversation.getConversationId(), sendMessageToConversation.getContent(), user.id());

        var conversation = dataProvider.getConversationById(sendMessageToConversation.getConversationId(), user.id());
        conversation
                .recipients()
                .stream()
                .filter(r -> !r.equals(user.id()))
                .map(r -> {
                    var messageToWebsocket = gson.toJson(new NewMessageInConversation(message));
                    return new BusMessage(messageToWebsocket, BusMessageType.MESSAGE_TO_WEBSOCKET, r);
                })
                .forEach(bus::pushOntoBus);

        return new SendMessageToConversationResponse(message);
    }

    // produces NewConversation for recipients
    private CreateConversationResponse createConversation(CreateConversation createConversation) {
        if(user == null) return null;

        var conversation = dataProvider.createNewConversation("TODO", createConversation.getRecipients(), user.id());

        conversation
                .recipients()
                .stream()
                .filter(r -> !r.equals(user.id()))
                .map(r -> {
                    var messageToWebsocket = gson.toJson(new NewConversation(conversation));
                    return new BusMessage(messageToWebsocket, BusMessageType.MESSAGE_TO_WEBSOCKET, r);
                })
                .forEach(bus::pushOntoBus);

        return new CreateConversationResponse(conversation);
    }
}
