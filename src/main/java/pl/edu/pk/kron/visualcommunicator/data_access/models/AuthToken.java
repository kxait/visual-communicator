package pl.edu.pk.kron.visualcommunicator.data_access.models;

import java.util.UUID;

public record AuthToken(UUID userId, String token) {}
