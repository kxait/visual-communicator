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

    public BusMessage pollByPredicate(Predicate<BusMessage> predicate) {
        var x = messages
                .stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
        if(x != null) {
            messages.remove(x);
            System.out.println("removed message " + x.type() + ", " + x.jsonContent());
            return x;
        }
        return null;
    }
}
