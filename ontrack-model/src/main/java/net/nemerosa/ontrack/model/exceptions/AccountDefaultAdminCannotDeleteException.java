package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class AccountDefaultAdminCannotDeleteException extends BaseException {

    public AccountDefaultAdminCannotDeleteException() {
        super("The default built-in administrator cannot be deleted.");
    }
}
