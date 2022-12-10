package pl.edu.pk.kron.visualcommunicator.common.model.message_contents;

import java.util.List;
import java.util.UUID;

public class Conversation {
    private final UUID id;
    private final String name;
    private final List<UUID> recipients;
    private final UUID author;

    public Conversation(UUID id, String name, List<UUID> recipients, UUID author) {
        this.id = id;
        this.name = name;
        this.recipients = recipients;
        this.author = author;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getRecipients() {
        return recipients;
    }

    public UUID getAuthor() {
        return author;
    }
}
