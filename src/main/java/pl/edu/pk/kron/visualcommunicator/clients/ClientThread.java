package pl.edu.pk.kron.visualcommunicator.clients;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Error;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Message;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Token;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.User;
import pl.edu.pk.kron.visualcommunicator.common.model.messages.*;

import java.util.Comparator;
import java.util.UUID;

public class ClientThread implements Runnable {
    private final MessageBus bus;
    private final UUID clientId;
    private final Gson gson;
    private User user;
    private final ClientDataProviderAdapter dataProvider;
    private final AuthenticatedUserRegistry userRegistry;

    public ClientThread(MessageBus bus, UUID clientId, ClientDataProviderAdapter dataProvider, AuthenticatedUserRegistry userRegistry) {
        this.bus = bus;
        this.clientId = clientId;
        this.dataProvider = dataProvider;
        this.userRegistry = userRegistry;
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
                    break;
                }
                continue;
            }

            var m = message.jsonContent();
            var abstractMessage = gson.fromJson(m, MessageFromWebsocket.class);
            var messageType = abstractMessage.getType();
            var response = switch(messageType) {
                case CLIENT_GET_AUTH -> getAuth(gson.fromJson(m, GetAuth.class));
                case CLIENT_GET_AUTH_TOKEN -> getAuthToken(gson.fromJson(m, GetAuthToken.class));
                case CLIENT_GET_CONVERSATIONS -> getConversations(gson.fromJson(m, GetConversations.class));
                case CLIENT_GET_MESSAGES -> getMessages(gson.fromJson(m, GetMessages.class));
                case CLIENT_SEND_MESSAGE -> sendMessageToConversation(gson.fromJson(m, SendMessageToConversation.class));
                case CLIENT_CREATE_NEW_CONVERSATION -> createConversation(gson.fromJson(m, CreateConversation.class));
                case CLIENT_WHO_AM_I -> whoAmI(gson.fromJson(m, WhoAmI.class));
                case CLIENT_GET_USERNAME_OF_USERID -> getUsernameOfUserId(gson.fromJson(m, GetUsernameOfUserId.class));
                default -> null;
            };

            if(response == null) {
                new Exception("unexpected messageType value " + messageType).printStackTrace();
                response = new Err(abstractMessage.getId(), new Error(true, ":)"));
            }

            var busMessage = new BusMessage(gson.toJson(response), BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
            bus.pushOntoBus(busMessage);
        }
        System.out.println("client thread for " + clientId + " killed");
        if(user != null)
            userRegistry.userLeft(user.id());
    }

    private GetUsernameOfUserIdResponse getUsernameOfUserId(GetUsernameOfUserId getUsernameOfUserId) {
        if(user == null)
            return null;

        var userWithName = dataProvider.getUserById(getUsernameOfUserId.getUserId());
        if(userWithName == null)
            return null;

        return new GetUsernameOfUserIdResponse(getUsernameOfUserId.getId(), userWithName.name());
    }

    private WhoAmIResponse whoAmI(WhoAmI whoAmI) {
        if(user == null)
            return null;
        return new WhoAmIResponse(whoAmI.getId(), user.id(), user.name());
    }

    private GetAuthResponse getAuth(GetAuth getAuth) {
        var user = dataProvider.getAuthByToken(getAuth.getToken());
        if(user != null && this.user == null) {
            this.user = user;
            userRegistry.newUserAuthenticated(clientId, user.id());
            return new GetAuthResponse(getAuth.getId(), user.id(), user.name());
        }
        return null;
    }

    private GetAuthTokenResponse getAuthToken(GetAuthToken getAuthToken) {
        var user = dataProvider.getUserByName(getAuthToken.getUserName());
        if(user != null && this.user == null && user.passwordHash().equals(getAuthToken.getPasswordHash())) {
            this.user = user;
            userRegistry.newUserAuthenticated(clientId, user.id());
            var token = dataProvider.getAuthTokenForUser(user.id());
            return new GetAuthTokenResponse(getAuthToken.getId(), new Token(token), user.id(), user.name());
        }
        return null;
    }

    private GetConversationsResponse getConversations(GetConversations getConversations) {
        if(user == null) return null;
        var conversations = dataProvider.getConversationsByUserId(user.id());
        return new GetConversationsResponse(getConversations.getId(), conversations);
    }

    private GetMessagesResponse getMessages(GetMessages getMessages) {
        if(user == null) return null;
        var messages = dataProvider.getMessagesByConversationId(getMessages.getConversationId(), user.id());
        var messagesSorted = messages.stream().sorted(Comparator.comparingLong(Message::millis)).toList();
        return new GetMessagesResponse(getMessages.getId(), messagesSorted);
    }

    // produces NewMessageInConversation for recipients
    private NewMessageInConversation sendMessageToConversation(SendMessageToConversation sendMessageToConversation) {
        if(user == null) return null;
        if(sendMessageToConversation.getContent().equals(""))
            return null;

        var message = dataProvider.newMessageInConversation(sendMessageToConversation.getConversationId(), sendMessageToConversation.getContent(), user.id());

        var conversation = dataProvider.getConversationById(sendMessageToConversation.getConversationId(), user.id());
        conversation
                .recipients()
                .stream()
                .filter(r -> !r.equals(user.id()))
                .filter(userRegistry::isUserIdLoggedIn)
                .map(r -> {
                    var clientId = userRegistry.getClientIdByUserId(r);
                    var messageToWebsocket = gson.toJson(new NewMessageInConversation(sendMessageToConversation.getId(), message));
                    return new BusMessage(messageToWebsocket, BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
                })
                .forEach(bus::pushOntoBus);

        return new NewMessageInConversation(sendMessageToConversation.getId(), message);
    }

    // produces NewConversation for recipients
    private CreateConversationResponse createConversation(CreateConversation createConversation) {
        if(user == null) return null;

        var conversation = dataProvider.createNewConversation(createConversation.getName(), createConversation.getRecipients(), user.id());

        conversation
                .recipients()
                .stream()
                .filter(r -> !r.equals(user.id()))
                .filter(userRegistry::isUserIdLoggedIn)
                .map(r -> {
                    var clientId = userRegistry.getClientIdByUserId(r);
                    var messageToWebsocket = gson.toJson(new NewConversation(createConversation.getId(), conversation));
                    return new BusMessage(messageToWebsocket, BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
                })
                .forEach(bus::pushOntoBus);

        return new CreateConversationResponse(createConversation.getId(), conversation);
    }
}
