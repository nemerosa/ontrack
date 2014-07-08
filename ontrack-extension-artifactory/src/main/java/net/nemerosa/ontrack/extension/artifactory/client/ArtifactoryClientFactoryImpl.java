package net.nemerosa.ontrack.extension.artifactory.client;

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.support.client.ClientConnection;
import net.nemerosa.ontrack.extension.support.client.ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtifactoryClientFactoryImpl implements ArtifactoryClientFactory {

    private final ClientFactory clientFactory;

    @Autowired
    public ArtifactoryClientFactoryImpl(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public ArtifactoryClient getClient(ArtifactoryConfiguration configuration) {
        return new ArtifactoryClientImpl(
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
