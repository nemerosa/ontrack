package net.nemerosa.ontrack.model.security;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

/**
 * Creation of a built-in account.
 */
@Data
public class AccountInput {

    @NotNull(message = "The account name is required.")
    @Pattern(regexp = "[a-zA-Z0-9_.-]+", message = "The account name must contain only letters, digits, underscores, dashes and dots.")
    private final String name;
    @NotNull(message = "The account full name is required.")
    @Size(min = 1, max = 100, message = "The account full name must be between 1 and 100 long.")
    private final String fullName;
    @NotNull(message = "The account email is required.")
    @Size(min = 1, max = 200, message = "The account email must be between 1 and 200 long.")
    private final String email;
    private final String password;

    /**
     * List of selected groups
     */
    private final Collection<Integer> groups;

}
