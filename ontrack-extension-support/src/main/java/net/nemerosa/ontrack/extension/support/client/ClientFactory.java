package net.nemerosa.ontrack.extension.support.client;

import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.OTHttpClient;

public interface ClientFactory {

    JsonClient getJsonClient(ClientConnection clientConnection);

    OTHttpClient getHttpClient(ClientConnection clientConnection);

}
