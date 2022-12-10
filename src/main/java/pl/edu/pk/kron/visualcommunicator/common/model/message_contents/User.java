package pl.edu.pk.kron.visualcommunicator.common.model.message_contents;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String name;

    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
