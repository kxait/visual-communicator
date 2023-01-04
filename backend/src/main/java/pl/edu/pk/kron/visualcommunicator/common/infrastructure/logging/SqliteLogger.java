package pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging;

import com.google.gson.Gson;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.sqlite.ConnectionProvider;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class SqliteLogger implements Logger {
    private final ConnectionProvider connectionProvider;
    private final Gson gson;

    private boolean tableExists = false;

    public SqliteLogger(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        gson = new Gson();
    }

    @Override
    public void log(LogSeverity severity, String format, Object... args) {
        connectionProvider.withConnection(connection -> {
            try {
                if(!tableExists) {
                    var checkStmt = connection.prepareStatement("SELECT * FROM sqlite_master WHERE name ='log' and type='table'");
                    var rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        tableExists = true;
                    }
                }
                if(!tableExists)
                    return null;

                var stmt = connection.prepareStatement("INSERT INTO log (date, format, argsJson, severity) VALUES" +
                        "(?, ?, ?, ?)");

                stmt.setLong(1, Instant.now().toEpochMilli());
                stmt.setString(2, format);
                stmt.setString(3, gson.toJson(args));
                stmt.setString(4, severity.toString());

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
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
