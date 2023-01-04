package pl.edu.pk.kron.visualcommunicator.common.model.message_contents;

import java.util.List;
import java.util.UUID;

public record Conversation(UUID id, String name, List<UUID> recipients, UUID author) {
}
