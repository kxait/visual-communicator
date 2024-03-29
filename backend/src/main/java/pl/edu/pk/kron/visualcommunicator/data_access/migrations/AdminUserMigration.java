package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class AdminUserMigration implements Migration {
    static class SqlStatements {
        public static final String AddAdminColumnToUsers = "ALTER TABLE user ADD COLUMN isAdmin INT";
        public static final String CreateAdminUser = "INSERT INTO user (id, name, passwordHash, isAdmin) VALUES" +
                "(?, 'admin', ?, 1)";
    }

    @Override
    public long getVersion() {
        return 3;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        var stmt = connection.prepareStatement(SqlStatements.AddAdminColumnToUsers);
        stmt.executeUpdate();
        stmt = connection.prepareStatement(SqlStatements.CreateAdminUser);
        var id = UUID.randomUUID().toString();
        var password = UUID.randomUUID().toString();
        stmt.setString(1, id);
        stmt.setString(2, password);
        stmt.executeUpdate();
        LogManager.instance().logWarning("'admin' user created with password '%s' - DO NOT LOSE IT!", password);
    }
}
