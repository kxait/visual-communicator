package pl.edu.pk.kron.visualcommunicator.common.infrastructure;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class MessageBus {
    private final List<BusMessage> messages;

    public MessageBus() {
        messages = new LinkedList<>();
    }

    public void pushOntoBus(BusMessage message) {
        messages.add(message);
        System.out.println("bus received message " + message.type() + ", " + message.jsonContent());
    }

    public synchronized BusMessage pollByPredicate(Predicate<BusMessage> predicate) {
        synchronized(messages) {
            for (var message : messages) {
                if (predicate.test(message)) {
                    messages.remove(message);
                    System.out.println("removed message " + message.type() + ", " + message.jsonContent());
                    return message;
                }
            }
            return null;
        }
    }
}
