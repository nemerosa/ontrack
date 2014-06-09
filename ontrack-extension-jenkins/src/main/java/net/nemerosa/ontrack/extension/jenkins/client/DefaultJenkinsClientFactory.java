package net.nemerosa.ontrack.extension.jenkins.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class DefaultJenkinsClientFactory implements JenkinsClientFactory {


    // Local cache for user data
    private final Cache<String, JenkinsClient> clientCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)
                    .build();

    @Override
    public JenkinsClient getClient(JenkinsConnection connection) {
        try {
            JenkinsClient client = clientCache.get(
                    connection.getUrl(),
                    () -> new DefaultJenkinsClient(connection)
            );
            if (client.hasSameConnection(connection)) {
                return client;
            } else {
                clientCache.invalidate(connection.getUrl());
                return clientCache.get(
                        connection.getUrl(),
                        () -> new DefaultJenkinsClient(connection)
                );
            }
        } catch (ExecutionException e) {
            throw new JenkinsClientCannotGetClientException(e, connection.getUrl());
        }
    }

}
