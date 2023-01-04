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
        if(userId == null)
            return null;
        return clientIdByUserId.get(userId);
    }

    public void newUserAuthenticated(UUID clientId, UUID userId) {
        if(clientId == null || userId == null)
            return;
        userIdByClientId.put(clientId, userId);
        clientIdByUserId.put(userId, clientId);
    }

    public void userLeft(UUID userId) {
        var clientId = clientIdByUserId.get(userId);
        if(userId == null || clientId == null)
            return;
        clientIdByUserId.remove(userId);
        userIdByClientId.remove(clientId);

    }

    public boolean isUserIdLoggedIn(UUID userId) {
        if(userId == null)
            return false;
        return clientIdByUserId.get(userId) != null;
    }
}
