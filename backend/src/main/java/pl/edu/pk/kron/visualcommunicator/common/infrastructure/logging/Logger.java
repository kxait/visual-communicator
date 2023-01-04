package pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging;

public interface Logger {
    void log(LogSeverity severity, String format, Object ... args);
    void logInfo(String format, Object ... args);
    void logWarning(String format, Object ... args);
    void logError(String format, Object ... args);
}
