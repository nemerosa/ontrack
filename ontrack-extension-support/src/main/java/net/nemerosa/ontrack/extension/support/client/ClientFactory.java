package net.nemerosa.ontrack.extension.support.client;

import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.OTHttpClient;

/**
 * @deprecated Will be removed in V5. Use the Spring REST template instead.
 */
@Deprecated
public interface ClientFactory {

    JsonClient getJsonClient(ClientConnection clientConnection);

    OTHttpClient getHttpClient(ClientConnection clientConnection);

}
