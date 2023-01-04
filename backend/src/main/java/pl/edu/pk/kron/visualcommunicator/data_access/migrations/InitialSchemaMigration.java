package pl.edu.pk.kron.visualcommunicator.data_access.migrations;
import java.sql.Connection;
import java.sql.SQLException;

class SqlStatements {
    public static final String CreateConversationRecipients = "CREATE TABLE IF NOT EXISTS conversation_recipient(\n" +
            "\tconversationId TEXT,\n" +
            "\tuserId TEXT,\n" +
            "\tPRIMARY KEY (conversationId, userId),\n" +
            "\tFOREIGN KEY (conversationId)\n" +
            "\t\tREFERENCES conversation (id)\n" +
            "\t\t\tON DELETE CASCADE\n" +
            "\t\t\tON UPDATE NO ACTION,\n" +
            "\tFOREIGN KEY (userId)\n" +
            "\t\tREFERENCES user (id)\n" +
            "\t\t\tON DELETE CASCADE\n" +
            "\t\t\tON UPDATE NO ACTION);";

    public static final String CreateAuthTokens = "CREATE TABLE IF NOT EXISTS auth_token(\n" +
            "\tuser_id INT,\n" +
            "\ttoken TEXT,\n" +
            "\tissued_millis INT,\n" +
            "\tFOREIGN KEY (user_id)\n" +
            "\t\tREFERENCES user (id)\n" +
            "\t\t\tON DELETE CASCADE\n" +
            "\t\t\tON UPDATE NO ACTION);";

    public static final String CreateConversations = "CREATE TABLE IF NOT EXISTS conversation(\n" +
            "\tid TEXT,\n" +
            "\tname TEXT,\n" +
            "\tauthorId TEXT,\n" +
            "\tPRIMARY KEY (id));";

    public static final String CreateMessages = "CREATE TABLE IF NOT EXISTS message(\n" +
            "\tid TEXT,\n" +
            "\tauthorId TEXT,\n" +
            "\tcontent TEXT,\n" +
            "\tconversationId TEXT,\n" +
            "\tmillis INT,\n" +
            "\tPRIMARY KEY (id));";

    public static final String CreateUsers = "CREATE TABLE IF NOT EXISTS user(\n" +
            "\tid TEXT,\n" +
            "\tname TEXT,\n" +
            "\tpasswordHash TEXT,\n" +
            "\tPRIMARY KEY (id));\n";
}

public class InitialSchemaMigration implements Migration{
    @Override
    public long getVersion() {
        return 2;
    }

    @Override
    public void perform(Connection connection) throws SQLException {
        connection.prepareStatement(SqlStatements.CreateUsers).execute();
        connection.prepareStatement(SqlStatements.CreateMessages).execute();
        connection.prepareStatement(SqlStatements.CreateConversations).execute();
        connection.prepareStatement(SqlStatements.CreateAuthTokens).execute();
        connection.prepareStatement(SqlStatements.CreateConversationRecipients).execute();
    }
}
