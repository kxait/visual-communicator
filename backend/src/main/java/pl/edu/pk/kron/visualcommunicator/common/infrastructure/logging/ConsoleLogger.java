package pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging;

public class ConsoleLogger implements Logger {
    @Override
    public void log(LogSeverity severity, String format, Object... args) {
        System.out.println("[" + severity.toString() + "] " + String.format(format, args));
    }

    @Override
    public void logInfo(String format, Object... args) {
        log(LogSeverity.INFO, format, args);
    }

    @Override
    public void logWarning(String format, Object... args) {
        log(LogSeverity.WARNING, format, args);
    }

    @Override
    public void logError(String format, Object... args) {
        log(LogSeverity.ERROR, format, args);
    }
}
