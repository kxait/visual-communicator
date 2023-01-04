package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogManager;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.sqlite.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

record Kvp<TKey, TValue>(TKey key, TValue value) {
}

class MigrationSqlStatements {
    public static final String CreateMigrations = "CREATE TABLE IF NOT EXISTS migration (" +
            "name TEXT," +
            "version INT)";

    public static final String InsertPerformedMigration = "INSERT INTO migration (name, version) VALUES (?, ?)";
    public static final String LastPerformedMigration = "SELECT MAX(version) as version FROM migration";
}

public class SqliteMigrationManager {
    private final ConnectionProvider connectionProvider;

    private final Dictionary<String, Migration> migrationsByName;

    public SqliteMigrationManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        migrationsByName = new Hashtable<>();
    }

    public void addMigration(String name, Migration value) {
        migrationsByName.put(name, value);
    }

    private void withConnection(Consumer<Connection> func) {
        connectionProvider.withConnection(connection -> { func.accept(connection); return null; });
    }

    public Exception performMigrations() {
        var lastMigration = new AtomicLong(-1L);
        withConnection(conn -> {
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement(MigrationSqlStatements.CreateMigrations);

                stmt.execute();
                stmt = conn.prepareStatement(MigrationSqlStatements.LastPerformedMigration);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    lastMigration.set(rs.getLong("version"));
                }
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        LogManager.instance().logInfo("last performed migration: %d", lastMigration.get());

        long finalLastMigration = lastMigration.get();
        var migrationsSortedByVersion = Collections.list(migrationsByName.keys())
                .stream()
                .map(name -> new Kvp<>(name, migrationsByName.get(name)))
                .sorted(Comparator.comparingLong(a -> (a.value().getVersion())))
                .filter(m -> m.value().getVersion() > finalLastMigration)
                .toList();

        var shouldBreak = new AtomicBoolean(false);
        for(var i : migrationsSortedByVersion) {
            if(shouldBreak.get() == true)
                break;
            withConnection(connection -> {
                try {
                    i.value().perform(connection);

                    var stmt = connection.prepareStatement(MigrationSqlStatements.InsertPerformedMigration);
                    stmt.setString(1, i.key());
                    stmt.setLong(2, i.value().getVersion());
                    stmt.executeUpdate();

                    LogManager.instance().logInfo("migration %s performed for update to version %d", i.key(), i.value().getVersion());
                }catch(SQLException e) {
                    LogManager.instance().logError("migration %s for version %d failed", i.key(), i.value().getVersion());
                    e.printStackTrace();
                    shouldBreak.set(true);
                }
            });
        }

        return shouldBreak.get() ? new RuntimeException("migration failed") : null;
    }


}
