package net.nemerosa.ontrack.extension.api.model;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class ExportRequestGroupingFormatException extends InputException {
    public ExportRequestGroupingFormatException(String grouping) {
        super("Wrong grouping specification: %s", grouping);
    }
}
