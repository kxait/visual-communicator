package pl.edu.pk.kron.visualcommunicator.data_access.models;

import java.util.UUID;

public record Message(UUID id, UUID authorUserId, String content, UUID conversationId) {
}
