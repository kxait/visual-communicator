package pl.edu.pk.kron.visualcommunicator.websocket;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.model.messages.MessageToClientCombined;

import java.util.Base64;

public class MessageSerializer {
    private final Gson gson;

    public MessageSerializer() {
        this.gson = new Gson();
    }

    public String serialize(MessageToClientCombined msg) {

        return gson.toJson(msg.getMessage().withId(msg.getMetadata().getId()));
    }

    public String serializeB64(MessageToClientCombined msg) {
        var serialized = serialize(msg);
        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }
}
