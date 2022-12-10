package pl.edu.pk.kron.visualcommunicator.infrastructure.mediator;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.Consumer;

public class WebMsgMediator {
    private final Dictionary<Class, Consumer> consumers;

    public WebMsgMediator() {
        consumers = new Hashtable<>();
    }

    public <T> void bind(Class<T> type, Consumer<T> onAction) {
        if(Collections.list(consumers.keys()).contains(type)) {
            System.out.println("mediator already contains handler for " + type.toString() + ", replacing");
        }
        consumers.put(type, onAction);
    }

    public <T> void sendMessage(Class<T> type, T message) {
        var consumer = consumers.get(type);
        if(consumer == null) throw new RuntimeException("consumer of type " + type.toString() + " not registered");
        ((Consumer<T>)consumer).accept(message);
    }
}
