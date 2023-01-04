package pl.edu.pk.kron.visualcommunicator.data_access.models;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogSeverity;

import java.time.Instant;

public record Log(Instant date, String format, String argsJson, LogSeverity severity) {}
