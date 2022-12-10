package pl.edu.pk.kron.visualcommunicator.model.messages;

import pl.edu.pk.kron.visualcommunicator.model.message_contents.Error;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class MessageFromClientMetadata {
    private final UUID id;
    private final Optional<UUID> from;
    private final Date timestamp;
    private final InetSocketAddress fromAddress;

    private final boolean handled;
    private final Error err;

    public MessageFromClientMetadata(UUID from, InetSocketAddress fromAddress) {
        this.id = UUID.randomUUID();
        this.from = Optional.of(from);
        this.timestamp = Date.from(Instant.now());
        this.handled = false;
        this.err = null;
        this.fromAddress = fromAddress;
    }

    public MessageFromClientMetadata(UUID from, UUID id, Date timestamp, boolean handled, Error err, InetSocketAddress fromAddress) {
        this.id = id;
        this.timestamp = timestamp;
        this.from = Optional.of(from);
        this.handled = handled;
        this.err = err;
        this.fromAddress = fromAddress;
    }

    public MessageFromClientMetadata(InetSocketAddress fromAddress) {
        this.id = UUID.randomUUID();
        this.from = Optional.empty();
        this.timestamp = Date.from(Instant.now());
        this.handled = false;
        this.err = null;
        this.fromAddress = fromAddress;
    }

    public MessageFromClientMetadata(UUID id, Date timestamp, boolean handled, Error err, InetSocketAddress fromAddress) {
        this.id = id;
        this.timestamp = timestamp;
        this.from = Optional.empty();
        this.handled = handled;
        this.err = err;
        this.fromAddress = fromAddress;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFrom() {
        return from.orElse(null);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isHandled() {
        return handled;
    }

    public Error getErr() {
        return err;
    }

    public InetSocketAddress getFromAddress() {
        return fromAddress;
    }
}
