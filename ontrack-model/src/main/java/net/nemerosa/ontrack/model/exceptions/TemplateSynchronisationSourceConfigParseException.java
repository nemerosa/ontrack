package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class TemplateSynchronisationSourceConfigParseException extends BaseException {
    public TemplateSynchronisationSourceConfigParseException(Exception e) {
        super(e, "Cannot parse template source configuration");
    }
}
