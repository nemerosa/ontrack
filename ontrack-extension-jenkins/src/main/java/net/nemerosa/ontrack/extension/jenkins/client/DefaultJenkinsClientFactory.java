package net.nemerosa.ontrack.extension.jenkins.client;

import net.nemerosa.ontrack.client.ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultJenkinsClientFactory implements JenkinsClientFactory {

    private final ClientFactory clientFactory;

    // TODO Local cache for clients

    @Autowired
    public DefaultJenkinsClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public JenkinsClient getClient(JenkinsConnection connection) {
        return new DefaultJenkinsClient(clientFactory.getJsonClient(connection.getClientConnection()));
    }

}
