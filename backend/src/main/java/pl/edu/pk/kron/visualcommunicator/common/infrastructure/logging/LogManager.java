package pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging;

import java.util.LinkedList;
import java.util.List;

public class LogManager implements Logger {
    private final List<Logger> targets;

    private static LogManager instance = null;

    public LogManager() {
        targets = new LinkedList<>();
        if(instance != null) {
            throw new RuntimeException("there can be only one logger");
        }
        instance = this;
    }

    public static LogManager instance() {
        return instance;
    }

    public void registerTarget(Logger logger) {
        targets.add(logger);
    }

    @Override
    public void log(LogSeverity severity, String format, Object... args) {
        for(var logger : targets) {

            for(var i = 0; i < args.length; i++) {
                var arg = args[i];
                if(arg instanceof String argString) {
                    if(argString.length() > 100) {
                        arg = argString.substring(0, 100) + "...";
                        args[i] = arg;
                    }
                }
            }

            logger.log(severity, format, args);
        }
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
