package pl.edu.pk.kron.visualcommunicator.model.messages;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MessageToClientMetadata {

    private final Date timestamp;
    private final CompletableFuture response;

    private boolean responded;
    private final UUID id;
    private final UUID toUserId;
    private final InetSocketAddress toAddress;

    public MessageToClientMetadata(UUID id, UUID toUserId) {
        this.timestamp = Date.from(Instant.now());
        this.response = new CompletableFuture();
        this.responded = false;
        this.id = id;
        this.toUserId = toUserId;
        toAddress = null;
    }

    public MessageToClientMetadata(UUID id, InetSocketAddress toAddress) {
        this.timestamp = Date.from(Instant.now());
        this.response = new CompletableFuture();
        this.responded = false;
        this.id = id;
        this.toAddress = toAddress;
        toUserId = null;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public CompletableFuture getResponse() {
        return response;
    }

    public boolean isResponded() {
        return responded;
    }

    public void responded() {
        responded = true;
        response.complete(null);
    }

    public UUID getId() {
        return id;
    }

    public UUID getToUserId() {
        return toUserId;
    }

    public InetSocketAddress getToAddress() {
        return toAddress;
    }
}
