package net.nemerosa.ontrack.acceptance.browser;

import java.io.IOException;

public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message, IOException ex) {
        super(message, ex);
    }
}
