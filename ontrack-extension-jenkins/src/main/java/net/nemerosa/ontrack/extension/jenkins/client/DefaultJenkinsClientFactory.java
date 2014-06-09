package net.nemerosa.ontrack.extension.jenkins.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.nemerosa.ontrack.client.ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class DefaultJenkinsClientFactory implements JenkinsClientFactory {

    private final ClientFactory clientFactory;

    // Local cache for clients
    private final Cache<String, JenkinsClient> clientCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)
                    .build();

    @Autowired
    public DefaultJenkinsClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public JenkinsClient getClient(JenkinsConnection connection) {
        try {
            JenkinsClient client = clientCache.get(
                    connection.getUrl(),
                    () -> new DefaultJenkinsClient(connection, clientFactory.getJsonClient(connection.getClientConnection()))
            );
            if (client.hasSameConnection(connection)) {
                return client;
            } else {
                clientCache.invalidate(connection.getUrl());
                return clientCache.get(
                        connection.getUrl(),
                        () -> new DefaultJenkinsClient(connection, clientFactory.getJsonClient(connection.getClientConnection()))
                );
            }
        } catch (ExecutionException e) {
            throw new JenkinsClientCannotGetClientException(e, connection.getUrl());
        }
    }

}
