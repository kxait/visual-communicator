package pl.edu.pk.kron.visualcommunicator.common.model.message_contents;

import java.util.UUID;

public record User(UUID id, String name, String passwordHash, boolean isAdmin) {
}
