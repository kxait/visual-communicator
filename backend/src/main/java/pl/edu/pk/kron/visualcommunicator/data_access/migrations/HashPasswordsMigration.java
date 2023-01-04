package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import pl.edu.pk.kron.visualcommunicator.common.Hasher;

import java.sql.Connection;
import java.sql.SQLException;

public class HashPasswordsMigration implements Migration {
    @Override
    public long getVersion() {
        return 4;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        var allUsers = connection.prepareStatement("SELECT id, passwordHash FROM user");
        var results = allUsers.executeQuery();

        while(results.next()) {
            var id = results.getString("id");
            var passwordHash = results.getString("passwordHash");

            var newPasswordHash = Hasher.sha256(passwordHash);

            var update = connection.prepareStatement("UPDATE user SET passwordHash = ? WHERE id = ?");
            update.setString(1, newPasswordHash);
            update.setString(2, id);
            update.executeUpdate();
        }
    }
}
