package net.nemerosa.ontrack.extension.jira.tx;

import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl;
import net.nemerosa.ontrack.extension.support.client.ClientConnection;
import net.nemerosa.ontrack.extension.support.client.ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JIRASessionFactoryImpl implements JIRASessionFactory {

    private final ClientFactory clientFactory;

    @Autowired
    public JIRASessionFactoryImpl(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public JIRASession create(JIRAConfiguration configuration) {
        // Creates a HTTP JSON client
        JsonClient jsonClient = clientFactory.getJsonClient(
                new ClientConnection(
                        configuration.getUrl(),
                        configuration.getUser(),
                        configuration.getPassword()
                )
        );
        // Creates the client
        JIRAClient client = new JIRAClientImpl(jsonClient);
        // Creates the session
        return new JIRASession() {

            @Override
            public JIRAClient getClient() {
                return client;
            }

            @Override
            public void close() {
                client.close();
            }
        };
    }

}
