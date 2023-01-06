package pl.edu.pk.kron.visualcommunicator.common.infrastructure.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;

public class ConnectionProvider {
    private final String connectionString;

    private final Object lock;

    public ConnectionProvider(String connectionString) {
        this.connectionString = connectionString;
        lock = 1;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    public synchronized <T> T withConnection(Function<Connection, T> func) {
        synchronized(lock) {
            try {
                var connection = getConnection();
                var result = func.apply(connection);
                connection.close();
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
