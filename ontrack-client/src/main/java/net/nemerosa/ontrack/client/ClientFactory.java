package net.nemerosa.ontrack.client;

public interface ClientFactory {

    JsonClient getJsonClient(ClientConnection connection);

}
