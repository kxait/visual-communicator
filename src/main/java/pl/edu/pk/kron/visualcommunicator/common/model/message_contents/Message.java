package pl.edu.pk.kron.visualcommunicator.common.model.message_contents;

import java.util.UUID;

public record Message(UUID id, UUID authorUserId, String content, UUID conversationId, long millis) {
}
