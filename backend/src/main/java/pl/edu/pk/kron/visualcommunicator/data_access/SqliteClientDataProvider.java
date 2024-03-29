package pl.edu.pk.kron.visualcommunicator.data_access;

import pl.edu.pk.kron.visualcommunicator.common.infrastructure.logging.LogSeverity;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.sqlite.ConnectionProvider;
import pl.edu.pk.kron.visualcommunicator.data_access.models.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class SqliteClientDataProvider implements ClientDataProvider {
    private final ConnectionProvider connectionProvider;
    private final Object lock;

    public SqliteClientDataProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        lock = 1;
    }

    private User getUser(ResultSet rs) throws SQLException {
        var id = UUID.fromString(rs.getString("id"));
        var name = rs.getString("name");
        var passwordHash = rs.getString("passwordHash");
        var isAdmin = rs.getBoolean("isAdmin");
        var activated = rs.getBoolean("activated");
        var profileData = rs.getString("profileData");

        return new User(id, name, passwordHash, isAdmin, activated, profileData);
    }

    public synchronized <T> T withConnection(Function<Connection, T> func) {
        return connectionProvider.withConnection(func);
    }

    @Override
    public Conversation getConversationById(UUID id, UUID sender) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement("SELECT * FROM conversation WHERE id = ? " +
                        "AND id IN (SELECT conversationId FROM conversation_recipient WHERE userId = ?)");
                stmt.setString(1, id.toString());
                stmt.setString(2, sender.toString());
                var rs = stmt.executeQuery();
                var hasRow = rs.next();
                if(!hasRow)
                    return null;

                var conversationId = UUID.fromString(rs.getString("id"));
                var name = rs.getString("name");
                var authorId = UUID.fromString(rs.getString("authorId"));

                var recipientsStmt = connection.prepareStatement("SELECT userId FROM conversation_recipient " +
                        "WHERE conversationId = ?");
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
                var stmt = connection.prepareStatement("SELECT * FROM message WHERE id = ? AND conversationId IN " +
                        "(SELECT conversationId FROM conversation_recipient WHERE userId = ?)");
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
                var stmt = connection.prepareStatement("SELECT * FROM user WHERE id = ?");
                stmt.setString(1, id.toString());
                var rs = stmt.executeQuery();
                if(!rs.next())
                    return null;

                return getUser(rs);
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
                var stmt = connection.prepareStatement("SELECT * FROM auth_token WHERE token = ?");
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
                var updateTokenStmt = connection.prepareStatement("UPDATE auth_token SET issued_millis = ? WHERE user_id = ? AND token = ?");
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
                var stmt = connection.prepareStatement("INSERT INTO auth_token (user_id, token, issued_millis) " +
                        "VALUES (?, ?, ?)");
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
                var stmt = connection.prepareStatement("SELECT * FROM message WHERE conversationId = ? " +
                        "AND conversationId IN (SELECT conversationId FROM conversation_recipient WHERE userId = ?)");
                stmt.setString(1, conversationId.toString());
                stmt.setString(2, sender.toString());
                var rs = stmt.executeQuery();
                var messages = new LinkedList<Message>();
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
                var stmt = connection.prepareStatement("SELECT * FROM conversation WHERE id IN " +
                        "(SELECT conversationId FROM conversation_recipient WHERE userId = ?)");
                stmt.setString(1, sender.toString());
                var rs = stmt.executeQuery();
                var conversations = new LinkedList<Conversation>();
                while(rs.next()) {
                    var conversationId = UUID.fromString(rs.getString("id"));
                    var name = rs.getString("name");
                    var authorId = UUID.fromString(rs.getString("authorId"));

                    var recipients = new LinkedList<UUID>();
                    var recipientsStmt = connection.prepareStatement("SELECT userId FROM conversation_recipient " +
                            "WHERE conversationId = ?");
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
                var stmt = connection.prepareStatement("SELECT * FROM user WHERE name LIKE ?");
                stmt.setString(1, String.format("%%%s%%", name));
                var rs = stmt.executeQuery();
                var users = new LinkedList<User>();
                while(rs.next()) {
                    var user = getUser(rs);
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
                var stmt = connection.prepareStatement("INSERT INTO user (id, name, passwordHash, isAdmin, activated, profileData) VALUES " +
                        "(?, ?, ?, ?, ?, '')");
                stmt.setString(1, id.toString());
                stmt.setString(2, name);
                stmt.setString(3, password);
                stmt.setBoolean(4, isAdmin);
                // activated
                stmt.setBoolean(5, false);
                stmt.execute();

                return new User(id, name, password, isAdmin, false, "");
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
                var stmt = connection.prepareStatement("SELECT * FROM user");
                var users = new LinkedList<User>();
                var rs = stmt.executeQuery();
                while(rs.next()) {
                    users.add(getUser(rs));
                }

                return users;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void setProfileData(UUID userId, String profileData) {
        withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement("UPDATE user SET profileData = ? WHERE id = ?");
                stmt.setString(1, profileData);
                stmt.setString(2, userId.toString());
                stmt.executeUpdate();

                return null;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public String getProfileData(UUID userId) {
        return withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement("SELECT profileData FROM user WHERE id = ?");
                stmt.setString(1, userId.toString());
                var rs = stmt.executeQuery();

                return rs.getString("profileData");
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
                var stmt = connection.prepareStatement("SELECT * FROM user WHERE name = ?");
                stmt.setString(1, name);
                var rs = stmt.executeQuery();
                if(!rs.next())
                    return null;

                return getUser(rs);
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void renameUser(UUID id, String newName) {
        withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement("UPDATE user SET name = ? WHERE id = ?");
                stmt.setString(1, newName);
                stmt.setString(2, id.toString());
                stmt.executeUpdate();
                return null;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void changeUserPassword(UUID id, String newPasswordHash) {
        withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement("UPDATE user SET passwordHash = ? WHERE id = ?");
                stmt.setString(1, newPasswordHash);
                stmt.setString(2, id.toString());
                stmt.executeUpdate();
                return null;
            }catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void setUserActivated(UUID id, boolean activated) {
        withConnection(connection -> {
            try {
                var stmt = connection.prepareStatement("UPDATE user SET activated = ? WHERE id = ?");
                stmt.setBoolean(1, activated);
                stmt.setString(2, id.toString());
                stmt.executeUpdate();
                return null;
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
                var stmt = connection.prepareStatement("INSERT INTO conversation (id, name, authorId) VALUES " +
                        "(?, ?, ?)");
                var id = UUID.randomUUID();
                stmt.setString(1, id.toString());
                stmt.setString(2, name);
                stmt.setString(3, author.toString());
                stmt.executeUpdate();

                for(var recipient : recipients) {
                    var recipientStmt = connection.prepareStatement("INSERT INTO conversation_recipient (conversationId, userId) VALUES " +
                            "(?, ?)");
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
                var stmt = connection.prepareStatement("INSERT INTO message (id, authorId, content, conversationId, millis) VALUES " +
                        "(?, ?, ?, ?, ?)");
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

    @Override
    public List<Log> getLogs(long count) {
        return withConnection(connection -> {
            try {
                var logs = new LinkedList<Log>();
                var stmt = connection.prepareStatement("SELECT date, format, argsJson, severity FROM log ORDER BY date DESC LIMIT ?");

                stmt.setLong(1, count);

                var rs = stmt.executeQuery();
                while(rs.next()) {
                    var date = Instant.ofEpochMilli(rs.getLong("date"));
                    var format = rs.getString("format");
                    var argsJson = rs.getString("argsJson");
                    var severity = rs.getString("severity");

                    logs.add(new Log(date, format, argsJson, LogSeverity.valueOf(severity)));
                }

                return logs;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new LinkedList<>();
        });
    }
}
