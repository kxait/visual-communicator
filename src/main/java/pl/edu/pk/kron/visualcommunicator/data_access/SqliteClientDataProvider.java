package pl.edu.pk.kron.visualcommunicator.data_access;

import pl.edu.pk.kron.visualcommunicator.data_access.models.AuthToken;
import pl.edu.pk.kron.visualcommunicator.data_access.models.Conversation;
import pl.edu.pk.kron.visualcommunicator.data_access.models.Message;
import pl.edu.pk.kron.visualcommunicator.data_access.models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

class SqlStatements {
    public static final String GetUsersByPartOfName = "SELECT * FROM user WHERE name LIKE ?";

    public static final String GetUserByName = "SELECT * FROM user WHERE name = ?";

    public static final String NewMessageInConversation = "INSERT INTO message (id, authorId, content, conversationId, millis) VALUES " +
            "(?, ?, ?, ?, ?)";

    public static final String GetMessagesByConversationId = "SELECT * FROM message WHERE conversationId = ? " +
            "AND conversationId IN (SELECT conversationId FROM conversation_recipient WHERE userId = ?)";

    public static final String InsertNewConversation = "INSERT INTO conversation (id, name, authorId) VALUES " +
            "(?, ?, ?)";

    public static final String InsertNewConversationRecipient = "INSERT INTO conversation_recipient (conversationId, userId) VALUES " +
            "(?, ?)";

    public static final String GetRecipientsByConversationId = "SELECT userId FROM conversation_recipient " +
            "WHERE conversationId = ?";

    public static final String GetConversationsByUserId = "SELECT * FROM conversation WHERE id IN " +
            "(SELECT conversationId FROM conversation_recipient WHERE userId = ?)";

    public static final String GetAuthTokenByTokenValue = "SELECT * FROM auth_token WHERE token = ?";

    // new millis, user id, token
    public static final String UpdateTokenValueByUserIdAndTokenValue = "UPDATE auth_token SET issued_millis = ? WHERE user_id = ? AND token = ?";

    public static final String GetConversationById = "SELECT * FROM conversation WHERE id = ? " +
            "AND id IN (SELECT conversationId FROM conversation_recipient WHERE userId = ?)";

    public static final String GetConversationRecipientsByConversationId = "SELECT userId FROM conversation_recipient " +
            "WHERE conversationId = ?";

    public static final String GetMessageById = "SELECT * FROM message WHERE id = ? AND conversationId IN " +
            "(SELECT conversationId FROM conversation_recipient WHERE userId = ?)";

    public static final String GetUserById = "SELECT * FROM user WHERE id = ?";

    public static final String InsertNewAuthToken = "INSERT INTO auth_token (user_id, token, issued_millis) " +
            "VALUES (?, ?, ?)";

    public static final String InsertNewUser = "INSERT INTO user (id, name, passwordHash, isAdmin) VALUES " +
            "(?, ?, ?, ?)";

    public static final String GetAllUsers = "SELECT * FROM user";
}

public class SqliteClientDataProvider implements ClientDataProvider {
    private final String connectionString;

    public SqliteClientDataProvider(String connectionString) {
        this.connectionString = connectionString;
    }

    private <T> T withConnection(Function<Connection, T> func) {
        try {
            var connection = getConnection();
            var result = func.apply(connection);
            connection.close();
            return result;
        }catch(SQLException e) {
            return null;
        }
    }

    public Exception connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            return e;
        }
        try {
            var ignored = DriverManager.getConnection(connectionString);
        }catch(SQLException e) {
            return e;
        }
        return null;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    @Override
    public Conversation getConversationById(UUID id, UUID sender) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetConversationById);
                stmt.setString(1, id.toString());
                stmt.setString(2, sender.toString());
                var rs = stmt.executeQuery();
                var hasRow = rs.next();
                if(!hasRow)
                    return null;

                var conversationId = UUID.fromString(rs.getString("id"));
                var name = rs.getString("name");
                var authorId = UUID.fromString(rs.getString("authorId"));

                var recipientsStmt = connection.prepareStatement(SqlStatements.GetConversationRecipientsByConversationId);
                recipientsStmt.setString(1, id.toString());
                var recipientsRs = recipientsStmt.executeQuery();

                var recipients = new LinkedList<UUID>();

                while(recipientsRs.next()) {
                    recipients.add(UUID.fromString(recipientsRs.getString("userId")));
                }

                return new Conversation(conversationId, name, recipients, authorId);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });

    }

    @Override
    public Message getMessageById(UUID id, UUID sender) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetMessageById);
                stmt.setString(1, id.toString());
                stmt.setString(2, sender.toString());

                var rs = stmt.executeQuery();

                if(!rs.next()) {
                    return null;
                }

                var authorId = UUID.fromString(rs.getString("authorId"));
                var content = rs.getString("content");
                var conversationId = UUID.fromString(rs.getString("conversationId"));
                var millis = rs.getLong("millis");

                return new Message(id, authorId, content, conversationId, millis);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public User getUserById(UUID id) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetUserById);
                stmt.setString(1, id.toString());
                var rs = stmt.executeQuery();
                if(!rs.next())
                    return null;

                var name = rs.getString("name");
                var passwordHash = rs.getString("passwordHash");
                var isAdmin = rs.getBoolean("isAdmin");

                return new User(id, name, passwordHash, isAdmin);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public User getAuthByToken(String token) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetAuthTokenByTokenValue);
                stmt.setString(1, token);
                var rs = stmt.executeQuery();
                if(!rs.next())
                    return null;

                var userId = UUID.fromString(rs.getString("user_id"));
                var millis = rs.getLong("issued_millis");

                // was the token issued before now minus 24h? (is the token older than 24h?)
                if(Instant.ofEpochMilli(millis).isBefore(Instant.now().minus(24, ChronoUnit.HOURS)))
                    return null;

                // update the token for 24h here
                var oneDayFromNow = Instant.now().plus(24, ChronoUnit.DAYS).toEpochMilli();
                var updateTokenStmt = connection.prepareStatement(SqlStatements.UpdateTokenValueByUserIdAndTokenValue);
                updateTokenStmt.setLong(1, oneDayFromNow);
                updateTokenStmt.setString(2, userId.toString());
                updateTokenStmt.setString(3, token);
                updateTokenStmt.executeUpdate();

                return getUserById(userId);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public AuthToken getNewAuthTokenForUser(UUID userId) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.InsertNewAuthToken);
                stmt.setString(1, userId.toString());
                var token = TokenGenerator.getNextAuthToken();
                stmt.setString(2, token);
                var millis = Instant.now().toEpochMilli();
                stmt.setLong(3, millis);

                stmt.executeUpdate();

                return new AuthToken(userId, token, millis);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public List<Message> getMessagesByConversationId(UUID conversationId, UUID sender) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetMessagesByConversationId);
                stmt.setString(1, conversationId.toString());
                stmt.setString(2, sender.toString());
                var rs = stmt.executeQuery();
                var messages = new LinkedList<Message>();
                if(!rs.next())
                    return messages;

                while(rs.next()) {
                    var messageId = UUID.fromString(rs.getString("id"));
                    var authorId = UUID.fromString(rs.getString("authorId"));
                    var content = rs.getString("content");
                    var millis = rs.getLong("millis");
                    messages.add(new Message(messageId, authorId, content, conversationId, millis));
                }

                return messages;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public List<Conversation> getConversationsByUserId(UUID sender) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetConversationsByUserId);
                stmt.setString(1, sender.toString());
                var rs = stmt.executeQuery();
                var conversations = new LinkedList<Conversation>();
                while(rs.next()) {
                    var conversationId = UUID.fromString(rs.getString("id"));
                    var name = rs.getString("name");
                    var authorId = UUID.fromString(rs.getString("authorId"));

                    var recipients = new LinkedList<UUID>();
                    var recipientsStmt = connection.prepareStatement(SqlStatements.GetRecipientsByConversationId);
                    recipientsStmt.setString(1, conversationId.toString());
                    var recipientsRs = recipientsStmt.executeQuery();
                    while(recipientsRs.next()) {
                        var recipientId = UUID.fromString(recipientsRs.getString("userId"));
                        recipients.add(recipientId);
                    }

                    var conversation = new Conversation(conversationId, name, recipients, authorId);
                    conversations.add(conversation);
                }

                return conversations;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public List<User> getUsersByPartOfName(String name) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetUsersByPartOfName);
                stmt.setString(1, String.format("%%%s%%", name));
                var rs = stmt.executeQuery();
                var users = new LinkedList<User>();
                while(rs.next()) {
                    var userId = UUID.fromString(rs.getString("id"));
                    var userName = rs.getString("name");
                    var passwordHash = rs.getString("passwordHash");
                    var isAdmin = rs.getBoolean("isAdmin");

                    var user = new User(userId, userName, passwordHash, isAdmin);
                    users.add(user);
                }

                return users;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public User createNewUser(String name, String password, boolean isAdmin) {
        return withConnection(connection -> {
            try {
                var id = UUID.randomUUID();
                var stmt = connection.prepareStatement(SqlStatements.InsertNewUser);
                stmt.setString(1, id.toString());
                stmt.setString(2, name);
                stmt.setString(3, password);
                stmt.setBoolean(4, isAdmin);
                stmt.execute();

                return new User(id, name, password, isAdmin);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public List<User> getAllUsers() {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetAllUsers);
                var users = new LinkedList<User>();
                var rs = stmt.executeQuery();
                while(rs.next()) {
                    var userId = UUID.fromString(rs.getString("id"));
                    var name = rs.getString("name");
                    var passwordHash = rs.getString("passwordHash");
                    var isAdmin = rs.getBoolean("isAdmin");

                    users.add(new User(userId, name, passwordHash, isAdmin));
                }

                return users;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public User getUserByName(String name) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.GetUserByName);
                stmt.setString(1, name);
                var rs = stmt.executeQuery();
                if(!rs.next())
                    return null;

                var userId = UUID.fromString(rs.getString("id"));
                var userName = rs.getString("name");
                var passwordHash = rs.getString("passwordHash");
                var isAdmin = rs.getBoolean("isAdmin");

                return new User(userId, userName, passwordHash, isAdmin);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public Conversation createNewConversation(String name, List<UUID> recipients, UUID author) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.InsertNewConversation);
                var id = UUID.randomUUID();
                stmt.setString(1, id.toString());
                stmt.setString(2, name);
                stmt.setString(3, author.toString());
                stmt.executeUpdate();

                for(var recipient : recipients) {
                    var recipientStmt = connection.prepareStatement(SqlStatements.InsertNewConversationRecipient);
                    recipientStmt.setString(1, id.toString());
                    recipientStmt.setString(2, recipient.toString());
                    recipientStmt.executeUpdate();
                }

                return new Conversation(id, name, recipients, author);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public Message newMessageInConversation(UUID conversationId, String content, UUID author) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement(SqlStatements.NewMessageInConversation);
                var id = UUID.randomUUID();
                stmt.setString(1, id.toString());
                stmt.setString(2, author.toString());
                stmt.setString(3, content);
                stmt.setString(4, conversationId.toString());
                var now = Instant.now().toEpochMilli();
                stmt.setLong(5, now);

                stmt.executeUpdate();

                return new Message(id, author, content, conversationId, now);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}