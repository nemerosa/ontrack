package net.nemerosa.ontrack.git;

import lombok.Data;

/**
 * Coordinates for a remote repository.
 */
@Data
public class GitRepository {

    /**
     * Remote URL-ish for the repository.
     */
    private final String remote;

}
