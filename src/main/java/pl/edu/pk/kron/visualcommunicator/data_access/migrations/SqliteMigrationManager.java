package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final String connectionString;

    private final Dictionary<String, Migration> migrationsByName;

    public SqliteMigrationManager(String connectionString) {
        this.connectionString = connectionString;

        migrationsByName = new Hashtable<>();
    }

    public void addMigration(String name, Migration value) {
        migrationsByName.put(name, value);
    }

    private void withConnection(Consumer<Connection> func) {
        try {
            var connection = DriverManager.getConnection(connectionString);
            func.accept(connection);
            connection.close();
        }catch(SQLException e) {
            return;
        }
    }

    public Exception performMigrations() {
        var lastMigration = -1L;
        try {
            var conn = DriverManager.getConnection(connectionString);

            var stmt = conn.prepareStatement(MigrationSqlStatements.CreateMigrations);
            stmt.execute();
            stmt = conn.prepareStatement(MigrationSqlStatements.LastPerformedMigration);
            var rs = stmt.executeQuery();
            if(rs.next()) {
                lastMigration = rs.getLong("version");
            }
            conn.close();
        } catch (SQLException e) {
            return e;
        }

        System.out.println("last performed migration: " + lastMigration);

        long finalLastMigration = lastMigration;
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

                    System.out.println("migration " + i.key() + " performed for update to version " + i.value().getVersion());
                }catch(SQLException e) {
                    System.out.println("migration " + i.key() + " for version " + i.value().getVersion() + " failed");
                    e.printStackTrace();
                    shouldBreak.set(true);
                }
            });
        }

        return shouldBreak.get() ? new RuntimeException("migration failed") : null;
    }


}
