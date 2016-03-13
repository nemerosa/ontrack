package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class PropertyTypeStorageReadException extends BaseException {

    public PropertyTypeStorageReadException(Class<?> type, Exception e) {
        super(
                e,
                "Could not parse JSON into type %s: %s",
                type.getName(),
                e.getMessage()
        );
    }
}
