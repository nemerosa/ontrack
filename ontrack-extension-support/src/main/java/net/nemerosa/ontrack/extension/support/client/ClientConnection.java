package net.nemerosa.ontrack.extension.support.client;

import lombok.Data;

@Data
public class ClientConnection {

    private final String url;
    private final String user;
    private final String password;

}
