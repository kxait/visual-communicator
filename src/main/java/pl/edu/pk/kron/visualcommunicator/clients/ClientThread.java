package pl.edu.pk.kron.visualcommunicator.clients;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessage;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.BusMessageType;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.*;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.Error;
import pl.edu.pk.kron.visualcommunicator.common.model.messages.*;

import java.util.Comparator;
import java.util.UUID;

class ErrOr<T extends MessageToWebsocket> {
    private final Err err;
    private final T value;

    public boolean isError() { return err != null; }

    public ErrOr(T value) {
        this.value = value;
        err = null;
    }

    public ErrOr(Err value) {
        this.err = value;
        this.value = null;
    }

    public Err getErr() {
        return err;
    }

    public T getValue() {
        return value;
    }
}

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
                case CLIENT_GET_AVAILABLE_MESSAGE_RECIPIENTS -> getAvailableMessageRecipients(gson.fromJson(m, GetAvailableMessageRecipients.class));
                case CLIENT_GET_USERS_BY_ID_OR_PART_OF_NAME -> getUsersByIdOrPartOfName(gson.fromJson(m, GetUsersByIdOrPartOfName.class));
                case CLIENT_ADMIN_CREATE_NEW_USER -> adminCreateNewUser(gson.fromJson(m, AdminCreateNewUser.class));
                case CLIENT_ADMIN_GET_ALL_USERS -> getAllUsers(gson.fromJson(m, AdminGetAllUsers.class));
                default -> null;
            };

            MessageToWebsocket concreteResponse = null;

            if(response == null) {
                concreteResponse = new Err(abstractMessage.getId(), new Error(true, ":)"));
            } else if(response.isError()) {
                concreteResponse = response.getErr();
            } else{
                concreteResponse = response.getValue();
            }

            var busMessage = new BusMessage(gson.toJson(concreteResponse), BusMessageType.MESSAGE_TO_WEBSOCKET, clientId);
            bus.pushOntoBus(busMessage);
        }
        System.out.println("client thread for " + clientId + " killed");
        if(user != null)
            userRegistry.userLeft(user.id());
    }

    private ErrOr<AdminGetAllUsersResponse> getAllUsers(AdminGetAllUsers getAllUsers) {
        if(user == null || user.isAdmin() == false)
            return err(getAllUsers.getId(), "must be admin");

        var users = dataProvider.getAllUsers();
        if(users == null)
            return err(getAllUsers.getId(), "something went wrong");

        return new ErrOr<>(new AdminGetAllUsersResponse(getAllUsers.getId(), users));
    }

    private ErrOr<GetUsersByIdOrPartOfNameResponse> getUsersByIdOrPartOfName(GetUsersByIdOrPartOfName getUsersByIdOrPartOfName) {
        if(user == null)
            return err(getUsersByIdOrPartOfName.getId(), "must be logged in");

        var users = dataProvider.getUsersByPartOfNameOrId(user.id(), getUsersByIdOrPartOfName.getInput());
        return new ErrOr<>(new GetUsersByIdOrPartOfNameResponse(getUsersByIdOrPartOfName.getId(), users
                .stream()
                .map(u -> new MessageRecipient(u.id(), u.name()))
                .toList()));
    }

    private ErrOr<GetAvailableMessageRecipientsResponse> getAvailableMessageRecipients(GetAvailableMessageRecipients getAvailableMessageRecipients) {
        if(user == null)
            return err(getAvailableMessageRecipients.getId(), "must be logged in");

        var recipients = dataProvider.getAvailableMessageRecipients(user.id());

        return new ErrOr<>(new GetAvailableMessageRecipientsResponse(
                getAvailableMessageRecipients.getId(),
                recipients
                        .stream()
                        .map(r -> new MessageRecipient(r.id(), r.name()))
                        .toList()));
    }

    private ErrOr<GetUsernameOfUserIdResponse> getUsernameOfUserId(GetUsernameOfUserId getUsernameOfUserId) {
        if(user == null)
            return err(getUsernameOfUserId.getId(), "must be logged in");

        var userWithName = dataProvider.getUserById(getUsernameOfUserId.getUserId());
        if(userWithName == null)
            return null;

        return new ErrOr<>(new GetUsernameOfUserIdResponse(getUsernameOfUserId.getId(), userWithName.name()));
    }

    private ErrOr<AdminCreateNewUserResponse> adminCreateNewUser(AdminCreateNewUser adminCreateNewUser) {
        if(user == null || user.isAdmin() == false)
            return err(adminCreateNewUser.getId(), "must be admin");

        var user = dataProvider.createNewUser(adminCreateNewUser.getUsername(), adminCreateNewUser.getPassword(), adminCreateNewUser.isAdmin());
        if(user == null)
            return err(adminCreateNewUser.getId(), "something went wrong");
        return new ErrOr<>(new AdminCreateNewUserResponse(adminCreateNewUser.getId(), user.id()));
    }

    private ErrOr<WhoAmIResponse> whoAmI(WhoAmI whoAmI) {
        if(user == null)
            return err(whoAmI.getId(), "must be logged in");
        return new ErrOr<>(new WhoAmIResponse(whoAmI.getId(), user.id(), user.name(), user.isAdmin()));
    }

    private ErrOr<GetAuthResponse> getAuth(GetAuth getAuth) {
        var user = dataProvider.getAuthByToken(getAuth.getToken());
        if(user != null && this.user == null) {
            this.user = user;
            userRegistry.newUserAuthenticated(clientId, user.id());
            return new ErrOr<>(new GetAuthResponse(getAuth.getId(), user.id(), user.name()));
        }
        return err(getAuth.getId(), "bad credentials");
    }

    private ErrOr<GetAuthTokenResponse> getAuthToken(GetAuthToken getAuthToken) {
        var user = dataProvider.getUserByName(getAuthToken.getUserName());
        if(user != null && this.user == null && user.passwordHash().equals(getAuthToken.getPasswordHash())) {
            this.user = user;
            userRegistry.newUserAuthenticated(clientId, user.id());
            var token = dataProvider.getAuthTokenForUser(user.id());
            return new ErrOr<>(new GetAuthTokenResponse(getAuthToken.getId(), new Token(token), user.id(), user.name()));
        }
        return err(getAuthToken.getId(), "bad token");
    }

    private ErrOr<GetConversationsResponse> getConversations(GetConversations getConversations) {
        if(user == null) return err(getConversations.getId(), "must be logged in");
        var conversations = dataProvider.getConversationsByUserId(user.id());
        return new ErrOr<>(new GetConversationsResponse(getConversations.getId(), conversations));
    }

    private ErrOr<GetMessagesResponse> getMessages(GetMessages getMessages) {
        if(user == null) return err(getMessages.getId(), "must be logged in");
        var messages = dataProvider.getMessagesByConversationId(getMessages.getConversationId(), user.id());
        var messagesSorted = messages.stream().sorted(Comparator.comparingLong(Message::millis)).toList();
        return new ErrOr<>(new GetMessagesResponse(getMessages.getId(), messagesSorted));
    }

    // produces NewMessageInConversation for recipients
    private ErrOr<NewMessageInConversation> sendMessageToConversation(SendMessageToConversation sendMessageToConversation) {
        if(user == null) return err(sendMessageToConversation.getId(), "must be logged in");
        if(sendMessageToConversation.getContent().equals(""))
            return err(sendMessageToConversation.getId(), "empty message body");

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

        return new ErrOr<>(new NewMessageInConversation(sendMessageToConversation.getId(), message));
    }

    // produces NewConversation for recipients
    private ErrOr<CreateConversationResponse> createConversation(CreateConversation createConversation) {
        if(user == null) return err(createConversation.getId(), "must be logged in");

        var conversation = dataProvider.createNewConversation(createConversation.getName(), createConversation.getRecipients(), user.id());
        if(conversation == null) return err(createConversation.getId(), "conversation already exists");

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

        return new ErrOr<>(new CreateConversationResponse(createConversation.getId(), conversation));
    }

    private <T extends MessageToWebsocket> ErrOr err(UUID id, String why) {
        return new ErrOr<T>(new Err(id, new Error(true, why)));
    }
}
