package net.nemerosa.ontrack.model.support;

import lombok.Data;
import lombok.ToString;

/**
 * Support class for a user name associated with a password
 */
@Data
@ToString(exclude = "password")
public class UserPassword {

    private final String user;
    private final String password;

}
