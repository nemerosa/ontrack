package net.nemerosa.ontrack.git;

import lombok.Data;

import java.util.Objects;

/**
 * Coordinates for a remote repository.
 */
@Data
public class GitRepository {

    /**
     * Remote URL-ish for the repository.
     */
    private final String remote;

    /**
     * User. Blank when no authorisation is needed.
     */
    private final String user;

    /**
     * Password
     */
    private final String password;

    public String getId() {
        return remote.replaceAll("[:\\.\\\\/@]", "_");
    }

}
