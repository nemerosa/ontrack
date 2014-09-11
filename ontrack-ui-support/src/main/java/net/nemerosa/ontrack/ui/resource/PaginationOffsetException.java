package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class PaginationOffsetException extends InputException {
    public PaginationOffsetException(int offset) {
        super("Incorrect offset: %d", offset);
    }
}
