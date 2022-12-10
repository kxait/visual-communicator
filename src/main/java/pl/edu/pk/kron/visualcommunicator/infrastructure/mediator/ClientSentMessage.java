package pl.edu.pk.kron.visualcommunicator.infrastructure.mediator;

import pl.edu.pk.kron.visualcommunicator.model.messages.MessageFromClientCombined;

public record ClientSentMessage(MessageFromClientCombined message) {
}
