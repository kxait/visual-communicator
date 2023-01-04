package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateIsUserActivatedMigration implements Migration {
    @Override
    public long getVersion() {
        return 5;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        var stmt = connection.prepareStatement("ALTER TABLE user ADD COLUMN activated INT");
        stmt.executeUpdate();
        stmt = connection.prepareStatement("UPDATE user SET activated = 1 WHERE 1 = 1");
        stmt.executeUpdate();
    }
}
