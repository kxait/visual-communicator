package pl.edu.pk.kron.visualcommunicator.clients;

import pl.edu.pk.kron.visualcommunicator.common.Hasher;
import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.*;
import pl.edu.pk.kron.visualcommunicator.data_access.ClientDataProvider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ClientDataProviderAdapter {
    private final ClientDataProvider provider;

    public ClientDataProviderAdapter(ClientDataProvider provider) {
        this.provider = provider;
    }

    private Conversation mapConversationToCommonModel(pl.edu.pk.kron.visualcommunicator.data_access.models.Conversation dalConversation) {
        return new Conversation(dalConversation.id(),
                dalConversation.name(),
                dalConversation.recipients(),
                dalConversation.author());
    }

    private Message mapMessageToCommonModel(pl.edu.pk.kron.visualcommunicator.data_access.models.Message dalMessage){
        return new Message(dalMessage.id(),
                dalMessage.authorUserId(),
                dalMessage.content(),
                dalMessage.conversationId(),
                dalMessage.millis());
    }

    private User mapUserToCommonModel(pl.edu.pk.kron.visualcommunicator.data_access.models.User dalUser) {
        return new User(dalUser.id(),
                dalUser.name(),
                dalUser.passwordHash(),
                dalUser.isAdmin(),
                dalUser.activated(),
                dalUser.profileData());
    }

    public Conversation getConversationById(UUID id, UUID sender) {
        return mapConversationToCommonModel(provider.getConversationById(id, sender));
    }
    public Message getMessageById(UUID id, UUID sender) {
        return mapMessageToCommonModel(provider.getMessageById(id, sender));
    }
    public User getUserById(UUID id) {
        return mapUserToCommonModel(provider.getUserById(id));
    }
    public User getAuthByToken(String token) {
        var user = provider.getAuthByToken(token);
        if(user == null) return null;
        return mapUserToCommonModel(user);
    }

    public String getAuthTokenForUser(UUID userId) {
        var token = provider.getNewAuthTokenForUser(userId);
        return token.token();
    }

    public List<Message> getMessagesByConversationId(UUID conversationId, UUID sender) {
        var messages = provider
                .getMessagesByConversationId(conversationId,sender);

        if(messages == null)
            return null;

        return messages
                .stream()
                .map(this::mapMessageToCommonModel)
                .toList();
    }
    public List<Conversation> getConversationsByUserId(UUID sender) {
        return provider
                .getConversationsByUserId(sender)
                .stream()
                .map(this::mapConversationToCommonModel)
                .toList();
    }

    public User getUserByName(String name) {
        var user = provider.getUserByName(name);
        if(user == null) return null;
        return mapUserToCommonModel(provider.getUserByName(name));
    }

    public Conversation createNewConversation(String name, List<UUID> recipients, UUID author) {
        var conversations = provider.getConversationsByUserId(author);
        if(conversations
                .stream()
                .anyMatch(c ->
                        new HashSet<>(c
                            .recipients())
                            .containsAll(recipients)
                        && new HashSet<>(recipients)
                                .containsAll(c.recipients())))
            return null;

        return mapConversationToCommonModel(provider.createNewConversation(name, recipients, author));
    }
    public Message newMessageInConversation(UUID conversationId, String content, UUID author) {
        return mapMessageToCommonModel(provider.newMessageInConversation(conversationId, content, author));
    }

    public List<User> getAvailableMessageRecipients(UUID sender) {
        var conversations = getConversationsByUserId(sender);
        return conversations
                .stream()
                .flatMap(x -> x.recipients().stream())
                .distinct()
                .filter(x -> !x.equals(sender))
                .map(this::getUserById)
                .toList();
    }

    public List<User> getUsersByPartOfNameOrId(UUID sender, String input) {
        if(input.length() < 3)
            return List.of();

        try {
            var uuid = UUID.fromString(input);
            // didnt fail - is an id!
            var user = getUserById(uuid);
            if(user != null) {
                // user exists!
                return List.of(user);
            }
        }catch(Exception ignored) { /* :( */ }

        return provider.getUsersByPartOfName(input)
                .stream()
                .map(this::mapUserToCommonModel)
                .filter(u -> !u.id().equals(sender))
                .toList();
    }

    public User createNewUser(String name, String password, boolean isAdmin) {
        var sha256Password = Hasher.sha256(password);

        var user = provider.createNewUser(name, sha256Password, isAdmin);
        if(user == null)
            return null;
        return mapUserToCommonModel(user);
    }

    public List<User> getAllUsers() {
        return provider.getAllUsers()
                .stream()
                .map(this::mapUserToCommonModel)
                .toList();
    }

    public void renameUser(UUID id, String newName) {
        provider.renameUser(id, newName);
    }

    public void changeUserPassword(UUID id, String newPasswordHash) {
        provider.changeUserPassword(id, newPasswordHash);
    }

    public void changeUserActivated(UUID id, boolean activated) {
        provider.setUserActivated(id, activated);
    }

    public String getUserProfileData(UUID userId) { return provider.getProfileData(userId); }

    public void setUserProfileData(UUID userId, String data) { provider.setProfileData(userId, data); }
}
