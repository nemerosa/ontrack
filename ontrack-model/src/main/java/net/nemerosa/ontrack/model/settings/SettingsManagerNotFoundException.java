package net.nemerosa.ontrack.model.settings;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class SettingsManagerNotFoundException extends NotFoundException {
    public SettingsManagerNotFoundException(String type) {
        super("Settings with type %s cannot be found", type);
    }
}
