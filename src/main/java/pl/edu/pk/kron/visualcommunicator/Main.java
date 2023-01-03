package pl.edu.pk.kron.visualcommunicator;
import pl.edu.pk.kron.visualcommunicator.clients.AuthenticatedUserRegistry;
import pl.edu.pk.kron.visualcommunicator.clients.ClientDataProviderAdapter;
import pl.edu.pk.kron.visualcommunicator.clients.ThreadOrchestratorThread;
import pl.edu.pk.kron.visualcommunicator.common.infrastructure.MessageBus;
import pl.edu.pk.kron.visualcommunicator.data_access.SqliteClientDataProvider;
import pl.edu.pk.kron.visualcommunicator.data_access.migrations.*;
import pl.edu.pk.kron.visualcommunicator.websocket.VisualCommunicatorWebsocketServer;
import pl.edu.pk.kron.visualcommunicator.websocket.WebsocketMessageSender;

import java.sql.SQLException;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8887;

    private static final String CONNECTION_STRING = "jdbc:sqlite:./data.db";

    public static void main(String[] args) {
        var exe = Executors.newCachedThreadPool();

        var bus = new MessageBus();

        var migrationManager = new SqliteMigrationManager(CONNECTION_STRING);

        migrationManager.addMigration("initial", new InitialSchemaMigration());
        migrationManager.addMigration("admin_user", new AdminUserMigration());
        migrationManager.addMigration("hash_passwords", new HashPasswordsMigration());
        migrationManager.addMigration("user_activated", new CreateIsUserActivatedMigration());
        migrationManager.addMigration("profile_data", new UserProfileDataMigration());
        migrationManager.addMigration("blank_json_profile_data", new BlankJsonUserProfileMigration());

        var e = migrationManager.performMigrations();
        if(e != null) {
            e.printStackTrace();
            System.out.println("could not perform migrations on " + CONNECTION_STRING + ", exiting");
            return;
        }

        var dataProvider = new SqliteClientDataProvider(CONNECTION_STRING);
        var providerAdapter = new ClientDataProviderAdapter(dataProvider);

        var userRegistry = new AuthenticatedUserRegistry();

        var threadOrchestrator = new ThreadOrchestratorThread(bus, providerAdapter, userRegistry);
        exe.submit(threadOrchestrator);

        var karol = new VisualCommunicatorWebsocketServer(PORT, bus);

        var websocketSender = new WebsocketMessageSender(bus, karol);
        exe.submit(websocketSender);

        karol.start();
    }
}