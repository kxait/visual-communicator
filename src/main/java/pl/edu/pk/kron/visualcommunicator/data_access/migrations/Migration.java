package pl.edu.pk.kron.visualcommunicator.data_access.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migration {
    long getVersion();
    void perform(Connection connection) throws SQLException;
}
