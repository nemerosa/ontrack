package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class ConfigurationNotFoundException extends NotFoundException {
    public ConfigurationNotFoundException(String name) {
        super("Configuration not found: %s.", name);
    }
}
