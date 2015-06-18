package net.nemerosa.ontrack.model.support;

import lombok.Data;

/**
 * Result of a connection test.
 */
@Data
public class ConnectionResult {

    public enum ConnectionResultType {
        OK, ERROR
    }

    private final ConnectionResultType type;
    private final String message;

    public static ConnectionResult ok() {
        return new ConnectionResult(ConnectionResultType.OK, "");
    }

    public static ConnectionResult error(String message) {
        return new ConnectionResult(ConnectionResultType.ERROR, message);
    }

}
