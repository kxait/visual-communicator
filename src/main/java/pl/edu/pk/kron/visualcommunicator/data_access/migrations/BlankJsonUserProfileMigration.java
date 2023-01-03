package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class BlankJsonUserProfileMigration implements Migration {
    @Override
    public long getVersion() {
        return 6;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        var stmt = connection.prepareStatement("UPDATE user SET profileData = '{}' WHERE 1=1");
        stmt.executeUpdate();
    }
}
