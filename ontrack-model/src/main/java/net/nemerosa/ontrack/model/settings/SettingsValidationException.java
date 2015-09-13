package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class SettingsValidationException extends InputException {
    public SettingsValidationException(Exception ex) {
        super("Cannot read the settings", ex.getMessage());
    }
}
