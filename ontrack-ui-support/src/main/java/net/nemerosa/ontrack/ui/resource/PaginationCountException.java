package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class PaginationCountException extends InputException {
    public PaginationCountException(int count) {
        super("Incorrect count: %d", count);
    }
}
