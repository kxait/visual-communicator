package pl.edu.pk.kron.visualcommunicator.data_access;

import pl.edu.pk.kron.visualcommunicator.data_access.models.*;

import java.util.List;
import java.util.UUID;

public interface ClientDataProvider {
    Conversation getConversationById(UUID id, UUID sender);
    Message getMessageById(UUID id, UUID sender);
    User getUserById(UUID id);
    User getAuthByToken(String token);
    AuthToken getNewAuthTokenForUser(UUID userId);
    List<Message> getMessagesByConversationId(UUID conversationId, UUID sender);
    List<Conversation> getConversationsByUserId(UUID sender);
    List<User> getUsersByPartOfName(String name);
    User createNewUser(String name, String password, boolean isAdmin);
    List<User> getAllUsers();

    User getUserByName(String name);

    void renameUser(UUID id, String newName);
    void changeUserPassword(UUID id, String newPasswordHash);
    void setUserActivated(UUID id, boolean activated);

    Conversation createNewConversation(String name, List<UUID> recipients, UUID author);
    Message newMessageInConversation(UUID conversationId, String content, UUID author);
}
