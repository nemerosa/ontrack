package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class AccountDefaultAdminCannotUpdateNameException extends BaseException {

    public AccountDefaultAdminCannotUpdateNameException() {
        super("The default built-in administrator cannot have its name changed.");
    }
}
