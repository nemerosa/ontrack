package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.Data;
import net.nemerosa.ontrack.client.ClientConnection;

/**
 * Jenkins connection parameters.
 */
@Data
public class JenkinsConnection {

    private final String url;
    private final String user;
    private final String password;

    public ClientConnection getClientConnection() {
        return new ClientConnection(
                url,
                user,
                password
        );
    }

}
