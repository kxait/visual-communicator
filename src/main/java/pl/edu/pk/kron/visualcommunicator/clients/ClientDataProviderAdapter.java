package pl.edu.pk.kron.visualcommunicator.clients;

import pl.edu.pk.kron.visualcommunicator.common.model.message_contents.*;
import pl.edu.pk.kron.visualcommunicator.data_access.ClientDataProvider;

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
                dalMessage.conversationId());
    }

    private User mapUserToCommonModel(pl.edu.pk.kron.visualcommunicator.data_access.models.User dalUser) {
        return new User(dalUser.id(),
                dalUser.name(),
                dalUser.passwordHash());
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
        return mapUserToCommonModel(provider.getAuthByToken(token));
    }

    public String getAuthTokenForUser(UUID userId) {
        var token = provider.getNewAuthTokenForUser(userId);
        return token.token();
    }

    public List<Message> getMessagesByConversationId(UUID conversationId, UUID sender) {
        return provider
                .getMessagesByConversationId(conversationId,sender)
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
        return mapConversationToCommonModel(provider.createNewConversation(name, recipients, author));
    }
    public Message newMessageInConversation(UUID conversationId, String content, UUID author) {
        return mapMessageToCommonModel(provider.newMessageInConversation(conversationId, content, author));
    }
}
