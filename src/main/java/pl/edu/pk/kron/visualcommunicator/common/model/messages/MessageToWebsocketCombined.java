package pl.edu.pk.kron.visualcommunicator.common.model.messages;

import java.util.UUID;

public record MessageToWebsocketCombined(MessageToWebsocket message, UUID to) {
}
