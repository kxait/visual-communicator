package pl.edu.pk.kron.visualcommunicator.data_access;

import pl.edu.pk.kron.visualcommunicator.data_access.models.Conversation;
import pl.edu.pk.kron.visualcommunicator.data_access.models.Message;
import pl.edu.pk.kron.visualcommunicator.data_access.models.User;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public class MockClientDataProvider implements ClientDataProvider {
    private final Dictionary<String, User> usersByAuthToken;
    private final Dictionary<UUID, List<Conversation>> conversationsByUserId;
    private final Dictionary<UUID, List<Message>> messagesByConversationId;

    public MockClientDataProvider() {
        usersByAuthToken = new Hashtable<>();
        usersByAuthToken.put(
                "1",
                new User(UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                "pierwszy"));
        usersByAuthToken.put(
                "2",
                new User(UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                "drugi"));

        var conversation = new Conversation(
                UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"),
                "pierwszy i drugi",
                List.of(
                        UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                        UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058")),
                UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"));

        conversationsByUserId = new Hashtable<>();
        conversationsByUserId.put(
                UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                List.of(conversation));
        conversationsByUserId.put(
                UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                List.of(conversation));

        messagesByConversationId = new Hashtable<>();
        messagesByConversationId.put(
                UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"),
                List.of(
                    new Message(
                            UUID.randomUUID(),
                            UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                            "pierwsza wiadomosc",
                            UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480")),
                    new Message(
                            UUID.randomUUID(),
                            UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                            "druga wiadomosc",
                            UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480")),
                    new Message(
                            UUID.randomUUID(),
                            UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                            "trzecia wiadomosc",
                            UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480")),
                    new Message(
                            UUID.randomUUID(),
                            UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                            "czwarta wiadomosc",
                            UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"))));
    }

    @Override
    public Conversation createNewConversation(UUID id, String name, List<UUID> recipients, UUID author) {
        return null;
    }

    @Override
    public Message sendMessageToConversation(UUID id, String content, UUID sender) {
        return null;
    }

    @Override
    public User getUserByAuthToken(String token) {
        return null;
    }

    @Override
    public List<Conversation> getConversations(UUID sender) {
        return null;
    }

    @Override
    public List<Message> getMessagesInConversation(UUID sender, UUID conversationId) {
        return null;
    }
}
