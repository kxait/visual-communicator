package pl.edu.pk.kron.visualcommunicator.infrastructure.mediator;

import java.net.InetSocketAddress;

public record ClientConnectedMessage(InetSocketAddress address) {
}
