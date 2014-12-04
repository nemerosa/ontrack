package net.nemerosa.ontrack.git;

import lombok.Data;

import java.util.Objects;

/**
 * Coordinates for a remote repository.
 */
@Data
public class GitRepository {

    /**
     * Type of repository (source of information)
     */
    private final String type;

    /**
     * Name of the repository
     */
    private final String name;

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
        return (type + "_" + name + "_" + remote).replaceAll("[:\\.\\\\/@]", "_");
    }

}
