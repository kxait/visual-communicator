package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class LoggerMigration implements Migration {
    @Override
    public long getVersion() {
        return 1;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        var stmt = connection.prepareStatement("CREATE TABLE log (" +
                "date INT," +
                "format TEXT," +
                "argsJson TEXT," +
                "severity TEXT)");
        stmt.execute();
    }
}
