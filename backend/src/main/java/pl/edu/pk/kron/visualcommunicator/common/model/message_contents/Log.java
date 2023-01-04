package pl.edu.pk.kron.visualcommunicator.common.model.message_contents;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogSeverity;

import java.time.Instant;

public record Log(Instant date, String format, Object[] args, LogSeverity severity, String message) {}
