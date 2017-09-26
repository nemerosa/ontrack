package net.nemerosa.ontrack.client;

import net.nemerosa.ontrack.common.BaseException;

public abstract class ClientException extends BaseException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Object... params) {
        super(message, params);
    }

    public ClientException(Exception error, String message, Object... params) {
        super(error, message, params);
    }
}
