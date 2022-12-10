package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import java.util.UUID;

public record MessageFromWebsocketCombined(MessageFromWebsocket message, UUID from) {
}
