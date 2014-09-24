package net.nemerosa.ontrack.extension.jenkins.client;

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.support.client.ClientConnection;
import net.nemerosa.ontrack.extension.support.client.ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultJenkinsClientFactory implements JenkinsClientFactory {

    private final ClientFactory clientFactory;

    @Autowired
    public DefaultJenkinsClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public JenkinsClient getClient(JenkinsConfiguration configuration) {
        return new DefaultJenkinsClient(
                clientFactory.getJsonClient(
                        new ClientConnection(
                                configuration.getUrl(),
                                configuration.getUser(),
                                configuration.getPassword()
                        )
                )
        );
    }

}
