package pl.edu.pk.kron.visualcommunicator.infrastructure.mediator;

import pl.edu.pk.kron.visualcommunicator.model.messages.MessageToClientCombined;

public record SendMessageToClient(MessageToClientCombined message) {
}
