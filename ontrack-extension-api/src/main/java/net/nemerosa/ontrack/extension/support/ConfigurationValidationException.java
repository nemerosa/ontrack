package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.exceptions.InputException;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;

/**
 * Thrown when a validation error occurs when creating or updating a configuration.
 *
 * @see AbstractConfigurationService#validateAndCheck(UserPasswordConfiguration)
 */
public class ConfigurationValidationException extends InputException {
    public ConfigurationValidationException(String message) {
        super(message);
    }
}
