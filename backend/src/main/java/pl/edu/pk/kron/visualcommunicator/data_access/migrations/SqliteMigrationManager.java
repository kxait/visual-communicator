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

    public void performMigrations() {
        var lastMigration = connectionProvider.withConnection(conn -> {
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement(MigrationSqlStatements.CreateMigrations);

                stmt.execute();
                stmt = conn.prepareStatement(MigrationSqlStatements.LastPerformedMigration);
                var rs = stmt.executeQuery();
                var result = -1L;
                if (rs.next()) {
                    result = rs.getLong("version");
                }
                conn.close();
                return result;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        LogManager.instance().logInfo("last performed migration: %d", lastMigration);

        var migrationsSortedByVersion = Collections.list(migrationsByName.keys())
                .stream()
                .map(name -> new Kvp<>(name, migrationsByName.get(name)))
                .sorted(Comparator.comparingLong(a -> (a.value().getVersion())))
                .filter(m -> m.value().getVersion() > lastMigration)
                .toList();

        LogManager.instance().logInfo("migrations to perform: %d", migrationsSortedByVersion.size());

        for (var i : migrationsSortedByVersion) {
            var result = connectionProvider.withConnection(connection -> {
                try {
                    //LogManager.instance().logInfo("performing migration %s for update to version %d", i.key(), i.value().getVersion());
                    i.value().perform(connection);

                    var stmt = connection.prepareStatement(MigrationSqlStatements.InsertPerformedMigration);
                    stmt.setString(1, i.key());
                    stmt.setLong(2, i.value().getVersion());
                    stmt.executeUpdate();

                    LogManager.instance().logInfo("migration %s performed for update to version %d", i.key(), i.value().getVersion());
                    return true;
                } catch (SQLException e) {
                    LogManager.instance().logError("migration %s for version %d failed", i.key(), i.value().getVersion());
                    e.printStackTrace();
                    return false;
                }
            });
            if (!result) {
                throw new RuntimeException("migration failed");
            }
        }
    }
}
