package net.nemerosa.ontrack.git;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Coordinates for a remote repository.
 */
@Data
@AllArgsConstructor
public class GitRepository {

    public static GitRepository empty() {
        return new GitRepository(null, null, null);
    }

    /**
     * Remote URL-ish for the repository.
     */
    @Wither
    private final String remote;

    /**
     * User. Blank when no authorisation is needed.
     */
    @Wither
    private final String user;

    /**
     * Password
     */
    @Wither
    private final String password;

}
