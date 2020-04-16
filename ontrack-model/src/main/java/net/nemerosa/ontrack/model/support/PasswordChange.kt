package net.nemerosa.ontrack.model.support;

import lombok.Data;

/**
 * Password change request.
 */
@Data
public class PasswordChange {

    private final String oldPassword;
    private final String newPassword;

}
