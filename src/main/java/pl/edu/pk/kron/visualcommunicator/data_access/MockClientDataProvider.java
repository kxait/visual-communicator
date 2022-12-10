package pl.edu.pk.kron.visualcommunicator.data_access;

import pl.edu.pk.kron.visualcommunicator.data_access.models.Conversation;
import pl.edu.pk.kron.visualcommunicator.data_access.models.Message;
import pl.edu.pk.kron.visualcommunicator.data_access.models.User;

import java.util.*;

public class MockClientDataProvider implements ClientDataProvider {
    private final Dictionary<UUID, Conversation> conversationsById;
    private final Dictionary<UUID, User> usersById;
    private final Dictionary<UUID, Message> messagesById;

    public MockClientDataProvider() {
        conversationsById = new Hashtable<>();
        conversationsById.put(UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"),
                new Conversation(
                    UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"),
                    "pierwszy i drugi",
                    List.of(
                            UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                            UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058")),
                    UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002")));

        usersById = new Hashtable<>();
        usersById.put(UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                new User(UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                    "pierwszy"));
        usersById.put(UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                new User(UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                    "drugi"));

        messagesById = new Hashtable<>();
        messagesById.put(UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"),
                new Message(
                    UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480"),
                    UUID.fromString("acb894d2-a8f2-4d76-bad6-326e96087058"),
                    "pierwsza wiadomosc",
                    UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480")));
        messagesById.put(UUID.fromString("cf906119-d321-4903-93ba-b177c208629c"),
                new Message(
                    UUID.fromString("cf906119-d321-4903-93ba-b177c208629c"),
                    UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                    "druga wiadomosc",
                    UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480")));
        messagesById.put(UUID.fromString("5ef8ec79-8d4d-423f-8347-6f46b70dfa6b"),
                new Message(
                    UUID.fromString("5ef8ec79-8d4d-423f-8347-6f46b70dfa6b"),
                    UUID.fromString("1c4c7732-789a-11ed-a1eb-0242ac120002"),
                    "trzecia wiadomosc",
                    UUID.fromString("70e24fee-8ad0-4b7c-a5d4-de43123dd480")));
    }

    @Override
    public Conversation getConversationById(UUID id, UUID sender) {
        return conversationsById.get(id);
    }

    @Override
    public Message getMessageById(UUID id, UUID sender) {
        return messagesById.get(id);
    }

    @Override
    public User getUserById(UUID id) {
        return usersById.get(id);
    }

    @Override
    public User getAuthByToken(String token) {
        int x;
        try {
            x = Integer.parseInt(token);
        }catch(Exception ignored){return null;}
        return usersById.get(x);
    }

    @Override
    public List<Message> getMessagesByConversationId(UUID conversationId, UUID sender) {
        return Collections
                .list(messagesById.elements())
                .stream()
                .filter(m -> m.conversationId().equals(conversationId))
                .toList();
    }

    @Override
    public List<Conversation> getConversationsByUserId(UUID sender) {
        return Collections
                .list(conversationsById.elements())
                .stream()
                .filter(c -> c.author().equals(sender))
                .toList();
    }

    @Override
    public List<User> getUsersByName(String name) {
        return Collections
                .list(usersById.elements())
                .stream()
                .filter(u -> u.name().equals(name))
                .toList();
    }

    @Override
    public Conversation createNewConversation(String name, List<UUID> recipients, UUID author) {
        var conversation = new Conversation(UUID.randomUUID(), name, recipients, author);
        conversationsById.put(conversation.id(), conversation);
        return conversation;
    }

    @Override
    public Message newMessageInConversation(UUID conversationId, String content, UUID author) {
        var message = new Message(UUID.randomUUID(), author, content, conversationId);
        messagesById.put(message.id(), message);
        return message;
    }
}
