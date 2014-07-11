package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class PreferencesNoAccountException extends BaseException {
    public PreferencesNoAccountException() {
        super("Cannot access preferences when not logged");
    }
}
