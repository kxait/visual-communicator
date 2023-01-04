package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class UserProfileDataMigration implements Migration {
    @Override
    public long getVersion() {
        return 6;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        var statement = connection.prepareStatement("ALTER TABLE user ADD COLUMN profileData TEXT");
        statement.executeUpdate();
        statement = connection.prepareStatement("UPDATE user SET profileData = '' WHERE 1 = 1");
        statement.executeUpdate();
    }
}
