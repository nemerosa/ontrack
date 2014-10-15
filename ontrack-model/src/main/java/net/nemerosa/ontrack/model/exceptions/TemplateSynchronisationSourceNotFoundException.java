package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class TemplateSynchronisationSourceNotFoundException extends BaseException {
    public TemplateSynchronisationSourceNotFoundException(String id) {
        super("Template synchronisation source not found: %s", id);
    }
}
