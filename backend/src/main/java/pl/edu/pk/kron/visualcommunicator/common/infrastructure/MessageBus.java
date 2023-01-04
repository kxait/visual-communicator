package pl.edu.pk.kron.visualcommunicator.common.infrastructure;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogManager;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class MessageBus {
    private final List<BusMessage> messages;

    public MessageBus() {
        messages = new LinkedList<>();
    }

    public synchronized void pushOntoBus(BusMessage message) {
        synchronized(messages) {
            messages.add(message);
            //LogManager.instance().logInfo("BUS RECV %s '%s'", message.type().toString(), message.jsonContent());
        }
    }

    public synchronized BusMessage pollByPredicate(Predicate<BusMessage> predicate) {
        synchronized(messages) {
            for (var message : messages) {
                if (predicate.test(message)) {
                    messages.remove(message);
                    //LogManager.instance().logInfo("BUS REM %s '%s'", message.type().toString(), message.jsonContent());
                    return message;
                }
            }
            return null;
        }
    }
}
