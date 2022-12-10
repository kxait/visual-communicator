package pl.edu.pk.kron.visualcommunicator.common.infrastructure;

import java.util.UUID;

public record BusMessage(String jsonContent, BusMessageType type, UUID recipientId) {}
