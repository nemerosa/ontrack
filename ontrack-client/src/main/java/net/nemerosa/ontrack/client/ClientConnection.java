package net.nemerosa.ontrack.client;

import lombok.Data;

@Data
public class ClientConnection {

    private final String url;
    private final String user;
    private final String password;

}
