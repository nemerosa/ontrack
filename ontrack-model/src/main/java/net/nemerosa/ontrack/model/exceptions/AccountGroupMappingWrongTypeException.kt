package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class AccountGroupMappingWrongTypeException extends BaseException {
    public AccountGroupMappingWrongTypeException(String mapping, String type) {
        super("Expected %s mapping but was %s", mapping, type);
    }
}
