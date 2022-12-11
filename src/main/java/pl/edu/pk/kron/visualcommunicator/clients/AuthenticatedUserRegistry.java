package pl.edu.pk.kron.visualcommunicator.clients;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public class AuthenticatedUserRegistry {
    private final Dictionary<UUID, UUID> userIdByClientId;
    private final Dictionary<UUID, UUID> clientIdByUserId;

    public AuthenticatedUserRegistry() {
        userIdByClientId = new Hashtable<>();
        clientIdByUserId = new Hashtable<>();
    }

    public UUID getUserIdByClientId(UUID clientId) {
        return userIdByClientId.get(clientId);
    }

    public UUID getClientIdByUserId(UUID userId){
        return clientIdByUserId.get(userId);
    }

    public void newUserAuthenticated(UUID clientId, UUID userId) {
        userIdByClientId.put(clientId, userId);
        clientIdByUserId.put(userId, clientId);
    }

    public void userLeft(UUID userId) {
        var clientId = clientIdByUserId.get(userId);
        clientIdByUserId.remove(userId);
        userIdByClientId.remove(clientId);
    }

    public boolean isUserIdLoggedIn(UUID userId) {
        return clientIdByUserId.get(userId) != null;
    }
}
