package pl.edu.pk.kron.visualcommunicator.data_access.models;

import java.util.UUID;

public record User(UUID id, String name, String passwordHash, boolean isAdmin) {
}
