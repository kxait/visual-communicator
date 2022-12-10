package pl.edu.pk.kron.visualcommunicator.data_access;

import pl.edu.pk.kron.visualcommunicator.data_access.models.*;

import java.util.List;
import java.util.UUID;

public interface ClientDataProvider {
    Conversation createNewConversation(UUID id, String name, List<UUID> recipients, UUID author);

    Message sendMessageToConversation(UUID id, String content, UUID sender);

    User getUserByAuthToken(String token);

    List<Conversation> getConversations(UUID sender);

    List<Message> getMessagesInConversation(UUID sender, UUID conversationId);
}
